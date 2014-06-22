package BussinessLogic.Common.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import Models.Atmosphera;

/**
 * Created by Solomiia on 6/13/2014.
 */
public class XmlBankParser {


    public String bank;
    public String city;
    public String address;
    public DataHelper _dataHelper;
    public String groupName;

    public XmlBankParser(String bank, String city, String address, String groupName)
    {
        this.bank = bank;
        this.address = address;
        _dataHelper = new DataHelper();
        this.city = _dataHelper.ChangeUkrainianLettersToEnglish(city);
        this.groupName = groupName;
    }

    public XmlBankParser(String bank, String city, String groupName)
    {
        this(bank, city, "", groupName);
    }
    public XmlBankParser(String bank, String city)
    {
         this(bank, city, "", "");
    }


    private Document XmlFromString(String xmlRecords)
    {

        DocumentBuilder db = null;
        Document doc = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));

            doc = db.parse(is);

        } catch (SAXException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (ParserConfigurationException e) {

            e.printStackTrace();
        }
        return doc;
    }

    public ArrayList<Atmosphera> readEntry(String xmlRecords) {
        Document doc = XmlFromString(xmlRecords);
        // todo: it is only good for PrivatBank ATM
        ArrayList<Node> atms = getAtm(doc);
        ArrayList<Atmosphera> atmospheras = new ArrayList<Atmosphera>();

        for (Node item : atms)
        {
            Atmosphera atmosphera = new Atmosphera();
            atmosphera.BankName = bank;
            atmosphera.City = city;
            atmosphera.GroupName = groupName;
            atmosphera.Street = getElementAttribute(item, "address_UA");

            if (!atmosphera.Street.equals(""))
            {
                atmospheras.add(atmosphera);
            }
        }

        return atmospheras;

    }

    private ArrayList<Node> getAtm(Document doc)
    {
        ArrayList<Node> originalNodeList = new ArrayList<Node>();
        NodeList nodes = doc.getElementsByTagName("atm");
        int numSections = nodes.getLength();

        int index = 0;

        for (int i = 0; i < numSections; i++)
        {
            Element node = (Element) nodes.item(i);
            Node child = node.getFirstChild();

           index = theLastChildNode(child, index++);


        }

        for (int j = 0; j < numSections; j++)
        {
            Node child = nodes.item(j);
            for (int i = 0; i< index; i++)
            {
                 child = child.getNextSibling();
            }
            if (getElementAttribute(child, "city_UA").equals(city) || getElementAttribute(child, "city_EN").equals(city)) {
                originalNodeList.add(child);
            }
        }

        return originalNodeList;


    }
    private int theLastChildNode(Node child, int index)
    {
        boolean flag =  child!= null && child.getNodeType() == Node.ELEMENT_NODE;
        if (flag)
        {
            child = child.getNextSibling();
            return theLastChildNode(child, index++);
        }
        else
        {
            return index--;
        }

    }




    public final static String getElementAttribute( Node elem, String attribure) {
        String data = "";
        if( elem != null && elem instanceof Element){

            data = ((Element)elem).getAttribute(attribure);
        }
        return data;
    }

}
