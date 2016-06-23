package com.empire.tigerlibrary.network.common;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ParsableObject {

	protected static HashMap<String, HashMap<String, String>> sFieldList = new HashMap<String, HashMap<String, String>>();

	public ParsableObject() {

		synchronized (sFieldList) {
			if (sFieldList.containsKey(getClass().getSimpleName()) == false) {
				HashMap<String, String> list = new HashMap<String, String>();
				Field[] fields = this.getClass().getFields();
				for (Field f : fields) {
					InjectVar anno = f.getAnnotation(InjectVar.class);
					if (anno != null) {
						String key = anno.value();
						list.put(key, f.getName());
					}
				}
				sFieldList.put(getClass().getSimpleName(), list);
			}
		}

	}

}
