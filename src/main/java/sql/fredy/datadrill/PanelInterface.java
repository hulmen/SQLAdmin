package sql.fredy.datadrill;

import org.jdom2.Element;


public interface PanelInterface {

    public String  getData();
    public Element getXML();
    public void    setAllValues(Element e);
    public void    loadData();
    public void    saveData();

}
