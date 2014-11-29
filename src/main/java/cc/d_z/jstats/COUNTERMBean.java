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
public class CounterMBean implements DynamicMBean {
    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return JStats.COUNTER_MAP.get(attribute).get();
    }

    public CounterMBean() {
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new RuntimeException("不支持修改操作");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attribute : attributes) {
            Counter counter = JStats.COUNTER_MAP.get(attribute);
            if (counter != null) {
                Attribute att = new Attribute(attribute, counter.get());
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
        Set<String> keys = JStats.COUNTER_MAP.keySet();
        ArrayList<MBeanAttributeInfo> mBeanAttributeInfos = new ArrayList<MBeanAttributeInfo>();
        for (String key : keys) {
            MBeanAttributeInfo mBeanAttributeInfo = new MBeanAttributeInfo(key, "java.lang.Long", key, true, false, false);
            mBeanAttributeInfos.add(mBeanAttributeInfo);
        }
        return new MBeanInfo(this.getClass().getName(), "Counter信息", mBeanAttributeInfos.toArray(new MBeanAttributeInfo[mBeanAttributeInfos.size()]), new MBeanConstructorInfo[]{new MBeanConstructorInfo("constructors", this.getClass().getConstructors()[0])}, new MBeanOperationInfo[0], new MBeanNotificationInfo[0]);
    }
}
