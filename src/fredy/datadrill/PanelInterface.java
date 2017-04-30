package sql.fredy.datadrill;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.Namespace;


public interface PanelInterface {

    public String  getData();
    public Element getXML();
    public void    setAllValues(Element e);
    public void    loadData();
    public void    saveData();

}
