package com.empire.tigerlibrary.network.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class MapParsableObject extends ParsableObject {

	public MapParsableObject() {
		super();
	}

	public Map<String,String> makeMapObject() {
		Map<String, String> params = new HashMap<String, String>();

		Field[] fields = getClass().getFields();
		for (Field field : fields) {
			InjectVar anno = field.getAnnotation(InjectVar.class);
			if (anno != null) {
				String key = anno.value();
				try {
					if (field.getType() == int.class) {
						params.put(key, String.valueOf(field.getInt(this)));
					} else if (field.getType() == long.class) {
						params.put(key, String.valueOf(field.getLong(this)));
					} else if (field.getType() == float.class) {
						params.put(key, String.valueOf(field.getFloat(this)));
					} else if (field.getType() == double.class) {
						params.put(key, String.valueOf(field.getDouble(this)));
					} else if (field.getType() == boolean.class) {
						params.put(key, String.valueOf(field.getBoolean(this)));
					} else if (field.getType() == String.class) {
						String strVelue = (String) field.get(this);
						if ( strVelue == null ) {
							strVelue = "";
						}
						params.put(key, strVelue);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}

	public void parse(HashMap<String,String> mapObject) throws
		NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {

		HashMap<String, String> fields;
		synchronized (sFieldList) {
			fields = sFieldList.get(getClass().getSimpleName());
			if (fields == null) {
				fields = new HashMap<String, String>();
				sFieldList.put(getClass().getSimpleName(), fields);
			}
		}

		Iterator iter = mapObject.keySet().iterator();
		while(iter.hasNext()) {

			String key = (String) iter.next();
			String fieldName = fields.get(key);
			Field field = this.getClass().getField(fieldName);

			if (field.getType() == int.class) {
				field.setInt(this, Integer.valueOf(mapObject.get(key)));
			} else if (field.getType() == long.class) {
				field.setLong(this, Long.valueOf(mapObject.get(key)));
			} else if (field.getType() == float.class) {
				field.setFloat(this, Float.valueOf(mapObject.get(key)));
			} else if (field.getType() == double.class) {
				field.setDouble(this, Double.valueOf(mapObject.get(key)));
			} else if (field.getType() == String.class) {
				field.set(this, mapObject.get(key));
			} else if (field.getType() == boolean.class) {
				field.setBoolean(this, Boolean.valueOf(mapObject.get(key)));
			}

		}

	}

}
