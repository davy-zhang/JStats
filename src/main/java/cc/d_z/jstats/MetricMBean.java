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
public class MetricMBean implements DynamicMBean {
    private static final String _count = "_count";
    private static final String _avg = "_avg";
    private static final String _max = "_max";
    private static final String _min = "_min";
    private static final String _sum = "_sum";


    @Override
    public Object getAttribute(String attribute) {
        if (attribute.endsWith(_count)) {
            return JStats.METRIC_MAP.get(attribute.substring(0, attribute.lastIndexOf(_count))).count.get();
        } else if (attribute.endsWith(_avg)) {
            return JStats.METRIC_MAP.get(attribute.substring(0, attribute.lastIndexOf(_avg))).getAvg();
        } else if (attribute.endsWith(_max)) {
            return JStats.METRIC_MAP.get(attribute.substring(0, attribute.lastIndexOf(_max))).max.get();
        } else if (attribute.endsWith(_min)) {
            return JStats.METRIC_MAP.get(attribute.substring(0, attribute.lastIndexOf(_min))).min.get();
        } else if (attribute.endsWith(_sum)) {
            return JStats.METRIC_MAP.get(attribute.substring(0, attribute.lastIndexOf(_sum))).sum.get();
        } else {
            return null;
        }
    }

    public MetricMBean() {
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new RuntimeException("不支持修改操作");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attribute : attributes) {
            Object metric = getAttribute(attribute);
            if (metric != null) {
                Attribute att = new Attribute(attribute, metric);
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
        Set<String> keys = JStats.METRIC_MAP.keySet();
        ArrayList<MBeanAttributeInfo> mBeanAttributeInfos = new ArrayList<MBeanAttributeInfo>();
        for (String key : keys) {
            MBeanAttributeInfo mBeanAttributeInfo_count = new MBeanAttributeInfo(key + _count, "java.lang.Long", key, true, false, false);
            MBeanAttributeInfo mBeanAttributeInfo_avg = new MBeanAttributeInfo(key + _avg, "java.lang.Double", key, true, false, false);
            MBeanAttributeInfo mBeanAttributeInfo_max = new MBeanAttributeInfo(key + _max, "java.lang.Long", key, true, false, false);
            MBeanAttributeInfo mBeanAttributeInfo_min = new MBeanAttributeInfo(key + _min, "java.lang.Long", key, true, false, false);
            MBeanAttributeInfo mBeanAttributeInfo_sum = new MBeanAttributeInfo(key + _sum, "java.lang.Long", key, true, false, false);
            mBeanAttributeInfos.add(mBeanAttributeInfo_count);
            mBeanAttributeInfos.add(mBeanAttributeInfo_avg);
            mBeanAttributeInfos.add(mBeanAttributeInfo_max);
            mBeanAttributeInfos.add(mBeanAttributeInfo_min);
            mBeanAttributeInfos.add(mBeanAttributeInfo_sum);
        }
        return new MBeanInfo(this.getClass().getName(), "Metric信息", mBeanAttributeInfos.toArray(new MBeanAttributeInfo[mBeanAttributeInfos.size()]), new MBeanConstructorInfo[]{new MBeanConstructorInfo("constructors", this.getClass().getConstructors()[0])}, new MBeanOperationInfo[0], new MBeanNotificationInfo[0]);
    }
}
