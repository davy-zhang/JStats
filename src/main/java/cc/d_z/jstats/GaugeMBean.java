package cc.d_z.jstats;

import javax.management.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author davy <br>
 *         2014-11-27 14:02 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class GaugeMBean implements DynamicMBean {
    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return JStats.GAUGE_MAP.get(attribute).get();
    }

    public GaugeMBean() {
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new RuntimeException("不支持修改操作");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attribute : attributes) {
            Gauge gauge = JStats.GAUGE_MAP.get(attribute);
            if (gauge != null) {
                Attribute att = new Attribute(attribute, gauge.get());
                attributeList.add(att);
            }
        }
        return attributeList;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        throw new RuntimeException("不支持修改操作");
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        Set<String> keys = JStats.GAUGE_MAP.keySet();
        ArrayList<MBeanAttributeInfo> mBeanAttributeInfos = new ArrayList<MBeanAttributeInfo>();
        for (String key : keys) {
            MBeanAttributeInfo mBeanAttributeInfo = new MBeanAttributeInfo(key, JStats.getGauge(key).get().getClass().getName(), key, true, false, false);
            mBeanAttributeInfos.add(mBeanAttributeInfo);
        }
        return new MBeanInfo(this.getClass().getName(), "Gauge信息", mBeanAttributeInfos.toArray(new MBeanAttributeInfo[mBeanAttributeInfos.size()]), new MBeanConstructorInfo[]{new MBeanConstructorInfo("constructors", this.getClass().getConstructors()[0])}, new MBeanOperationInfo[0], new MBeanNotificationInfo[0]);
    }
}
