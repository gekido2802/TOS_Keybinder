import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class XMLParser {

    // READ ALL HOTKEYS FROM AN XML FILE
    public static List<HotKey> parse(InputStream inputStream) {
        Document doc = null;
        List<HotKey> hotKeys = new ArrayList<>();

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            JOptionPane.showMessageDialog(null, e);
            System.exit(0);
        }

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("HotKey");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            HotKey hotKey = new HotKey();

            hotKey.setId(element.getAttribute("ID"));
            hotKey.setName(element.getAttribute("Name"));
            hotKey.setKey(element.getAttribute("Key"));
            hotKey.setDownScp(element.getAttribute("DownScp"));
            hotKey.setUpScp(element.getAttribute("UpScp"));
            hotKey.setUseShift(element.getAttribute("UseShift").equals("YES"));
            hotKey.setUseAlt(element.getAttribute("UseAlt").equals("YES"));
            hotKey.setUseCtrl(element.getAttribute("UseCtrl").equals("YES"));
            hotKey.setOnEdit(element.getAttribute("OnEdit").equals("YES"));

            hotKeys.add(hotKey);
        }

        Collections.sort(hotKeys);
        return hotKeys;
    }

    // SAVE ALL HOTKEYS TO AN XML FILE
    public static boolean save(List<HotKey> hotKeys, OutputStream outputStream) {
        Document doc;
        Transformer transformer;

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        Element root = (Element) doc.appendChild(doc.createElement("Category"));

        for (HotKey h : hotKeys) {
            Element hotkey = (Element) root.appendChild(doc.createElement("HotKey"));

            hotkey.setAttribute("ID", h.getId());
            hotkey.setAttribute("Name", h.getName());
            hotkey.setAttribute("DownScp", h.getDownScp());
            hotkey.setAttribute("UpScp", h.getUpScp());
            hotkey.setAttribute("Key", h.getKey());
            hotkey.setAttribute("UseShift", h.useShift() ? "YES" : "NO");
            hotkey.setAttribute("UseAlt", h.useAlt() ? "YES" : "NO");
            hotkey.setAttribute("UseCtrl", h.useCtrl() ? "YES" : "NO");
            hotkey.setAttribute("OnEdit", h.isOnEdit() ? "YES" : "NO");
        }

        try {
            transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        } catch (TransformerException e) {
            JOptionPane.showMessageDialog(null, e);
            return false;
        }

        return true;
    }
}
