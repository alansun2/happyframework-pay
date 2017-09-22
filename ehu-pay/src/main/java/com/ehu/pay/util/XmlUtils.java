package com.ehu.pay.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;



public class XmlUtils {
	/**
	 * 瑙ｆ瀽xml,杩斿洖绗竴绾у厓绱犻敭鍊煎銆傚鏋滅锟�?绾у厓绱犳湁瀛愯妭鐐癸紝鍒欐鑺傜偣鐨勶拷?锟芥槸瀛愯妭鐐圭殑xml鏁版嵁锟�?
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map<String,String> doXMLParse(String strxml) throws JDOMException, IOException {

		if(null == strxml || "".equals(strxml)) {
			return null;
		}

		Map<String,String> m = new HashMap<String,String>();

		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if(children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = XmlUtils.getChildrenText(children);
			}

			m.put(k, v);
		}
		//鍏抽棴
		in.close();
		return m;
	}

	/**
	 * 鑾峰彇瀛愮粨鐐圭殑xml
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if(!children.isEmpty()) {
			Iterator it = children.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if(!list.isEmpty()) {
					sb.append(XmlUtils.getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}
	public static String createXml(String xml){
		String[] xmls = xml.split(",");
		Element root = new Element("xml");  
		Document document = new Document(root);  
		Element return_code = new Element("return_code");
		return_code.setText(xmls[0]);
		root.addContent(return_code);  
		Element return_msg = new Element("return_msg");  
		return_msg.setText(xmls[1]);
		root.addContent(return_msg);  
		XMLOutputter XMLOut = new XMLOutputter(); 
		 String resultXml = "";
        try {  
            Format f = Format.getPrettyFormat();  
            f.setEncoding("UTF-8");//default=UTF-8  
            XMLOut.setFormat(f);  
            resultXml = XMLOut.outputString(document); 
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		return resultXml;
	}

}
