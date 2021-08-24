package com.buaa.qp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassParser {
    public ArrayList<String> toStringList(Object object) throws ClassCastException {
        ArrayList<Object> objectList = toObjectList(object);
        if (objectList == null)
            return null;
        ArrayList<String> stringList = new ArrayList<>();
        for (Object obj : objectList) {
            String str = obj.toString();
            if (!str.isEmpty())
                stringList.add(obj.toString());
        }
        return stringList;
    }

    public ArrayList<Integer> toIntegerList(Object object) throws ClassCastException, NumberFormatException {
        ArrayList<Object> objectList = toObjectList(object);
        if (objectList == null)
            return null;
        ArrayList<Integer> integerList = new ArrayList<>();
        for (Object obj : objectList)
            integerList.add(Integer.parseInt(obj.toString()));
        return integerList;
    }

    public ArrayList<Map<String, Object>> toMapList(Object object) throws ClassCastException {
        ArrayList<Object> objectList = toObjectList(object);
        if (objectList == null)
            return null;
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (Object obj : objectList) {
            mapList.add(toMap(obj));
        }
        return mapList;
    }

    public ArrayList<Object> toObjectList(Object object) throws ClassCastException {
        if (object == null)
            return null;
        if (!(object instanceof List))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        List<Object> objectList = (List<Object>) object;
        ArrayList<Object> noNullList = new ArrayList<>();
        for (Object obj : objectList) {
            if (obj != null)
                noNullList.add(obj);
        }
        return noNullList;
    }

    public Map<String, Object> toMap(Object object) throws ClassCastException {
        if (object == null)
            return null;
        if (!(object instanceof Map))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = new HashMap<>((Map<String, Object>) object);
        return map;
    }
}
