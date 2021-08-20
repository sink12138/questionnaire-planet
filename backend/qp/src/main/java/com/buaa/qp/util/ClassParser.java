package com.buaa.qp.util;

import java.util.ArrayList;
import java.util.Map;

public class ClassParser {
    public ArrayList<String> toStringList(Object object) throws ClassCastException {
        if (object == null)
            return null;
        if (!(object instanceof ArrayList))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        ArrayList<Object> objectList = (ArrayList<Object>) object;
        ArrayList<String> stringList = new ArrayList<>();
        for (Object obj : objectList) {
            String str = obj.toString();
            if (!str.isEmpty())
                stringList.add(obj.toString());
        }
        return stringList;
    }

    public ArrayList<Integer> toIntegerList(Object object) throws ClassCastException, NumberFormatException {
        if (object == null)
            return null;
        if (!(object instanceof ArrayList))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        ArrayList<Object> objectList = (ArrayList<Object>) object;
        ArrayList<Integer> integerList = new ArrayList<>();
        for (Object obj : objectList)
            integerList.add(Integer.parseInt(obj.toString()));
        return integerList;
    }

    public ArrayList<Map<String, Object>> toMapList(Object object) throws ClassCastException {
        if (object == null)
            return null;
        if (!(object instanceof ArrayList))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        ArrayList<Object> objectList = (ArrayList<Object>) object;
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (Object obj : objectList) {
            mapList.add(toMap(obj));
        }
        return mapList;
    }

    public Map<String, Object> toMap(Object object) throws ClassCastException {
        if (object == null)
            return null;
        if (!(object instanceof Map))
            throw new ClassCastException();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map;
    }
}
