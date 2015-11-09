import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class XMLParser extends DefaultHandler {

    // FIELDS
    private static XMLParser instance;
    private List<HotKey> list;

    private XMLParser() {
    }

    // READ ALL HOTKEYS FROM AN XML FILE
    public static List<HotKey> parse(String fileName) {

        instance = instance != null ? instance : (instance = new XMLParser());

        XMLReader reader;

        try {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setContentHandler(instance);
            reader.parse(new InputSource(new FileReader(fileName)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return instance.list;
    }

    // SAVE ALL HOTKEYS TO AN XML FILE
    public static boolean save(List<HotKey> hotKeys, String fileName) {
        Document doc;
        Transformer transformer;

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        Element root = doc.createElement("Category");
        doc.appendChild(root);

        for (HotKey h : hotKeys) {
            Element hotkey = doc.createElement("HotKey");

            hotkey.setAttribute("ID", h.getId());
            hotkey.setAttribute("Name", h.getName());
            hotkey.setAttribute("DownScp", h.getDownScp());
            hotkey.setAttribute("UpScp", h.getUpScp());
            hotkey.setAttribute("Key", h.getKey());
            hotkey.setAttribute("UseShift", h.useShift() ? "YES" : "NO");
            hotkey.setAttribute("UseAlt", h.useAlt() ? "YES" : "NO");
            hotkey.setAttribute("UseCtrl", h.useCtrl() ? "YES" : "NO");
            hotkey.setAttribute("OnEdit", h.isOnEdit() ? "YES" : "NO");

            root.appendChild(hotkey);
        }

        try {
            transformer.transform(new DOMSource(doc), new StreamResult(new File(fileName)));
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // INITIALIZATION
    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<>();
    }

    // BEGINNING TAG
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (!qName.equals("HotKey"))
            return;

        HotKey hotKey = new HotKey();

        for (int i = 0; i < attributes.getLength(); i++) {

            String value = attributes.getValue(i);

            switch (attributes.getQName(i).toUpperCase()) {
                case "ID":
                    hotKey.setId(value);
                    break;
                case "NAME":
                    hotKey.setName(value);
                    break;
                case "DOWNSCP":
                    hotKey.setDownScp(value);
                    break;
                case "UPSCP":
                    hotKey.setUpScp(value);
                    break;
                case "KEY":
                    hotKey.setKey(value);
                    break;
                case "USERSHIFT":
                    hotKey.setUseShift(value.toUpperCase().equals("YES"));
                    break;
                case "USEALT":
                    hotKey.setUseAlt(value.toUpperCase().equals("YES"));
                    break;
                case "USECTRL":
                    hotKey.setUseCtrl(value.toUpperCase().equals("YES"));
                    break;
                case "ONEDIT":
                    hotKey.setOnEdit(value.toUpperCase().equals("YES"));
                    break;
            }
        }

        list.add(hotKey);
    }
}
