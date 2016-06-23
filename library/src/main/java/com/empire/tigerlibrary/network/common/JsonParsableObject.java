package com.empire.tigerlibrary.network.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class JsonParsableObject extends ParsableObject {

	public JsonParsableObject() {
		super();
	}

	public JSONObject makeJSONObject() {
		JSONObject jSONObject = new JSONObject();

		Field[] fields = getClass().getFields();
		for (Field field : fields) {
			InjectVar anno = field.getAnnotation(InjectVar.class);
			if (anno != null) {
				String key = anno.value();
				try {
					if (field.getType() == int.class) {
						jSONObject.put(key, field.getInt(this));
					} else if (field.getType() == long.class) {
						jSONObject.put(key, field.getLong(this));
					} else if (field.getType() == float.class) {
						jSONObject.put(key, field.getFloat(this));
					} else if (field.getType() == double.class) {
						jSONObject.put(key, field.getDouble(this));
					} else if (field.getType() == boolean.class) {
						jSONObject.put(key, field.getBoolean(this));
					} else if (field.getType() == String.class) {
						String strVelue = (String) field.get(this);
						if ( strVelue == null ) {
							strVelue = "";
						}
						jSONObject.put(key, strVelue);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
		return jSONObject;
	}



	public void parse(JSONObject jsonObject) throws
		NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
		JSONException, InstantiationException {

		HashMap<String, String> fields;
		synchronized (sFieldList) {
			fields = sFieldList.get(getClass().getSimpleName());
			if (fields == null) {
				fields = new HashMap<String, String>();
				sFieldList.put(getClass().getSimpleName(), fields);
			}
		}

		Iterator iter = jsonObject.keys();
		while(iter.hasNext()) {

			String key = (String) iter.next();
			String fieldName = fields.get(key);
			if(fieldName == null || jsonObject.isNull(key) ){
				continue;
			}
			Field field = this.getClass().getField(fieldName);

			if (field.getType() == int.class) {
				field.setInt(this, jsonObject.getInt(key));
			} else if (field.getType() == long.class) {
				field.setLong(this, jsonObject.getLong(key));
			} else if (field.getType() == float.class) {
				field.setFloat(this, Float.valueOf(jsonObject.getString(key)));
			} else if (field.getType() == double.class) {
				field.setDouble(this, Double.valueOf(jsonObject.getString(key)));
			} else if (field.getType() == String.class) {
				field.set(this, jsonObject.getString(key));
			} else if (field.getType() == boolean.class) {
				field.setBoolean(this, jsonObject.getBoolean(key));
			} else if (List.class.isAssignableFrom(field.getType())) {
				Type t = field.getGenericType();
				ParameterizedType pt = (ParameterizedType) t;
				Class<?> c = (Class<?>) pt.getActualTypeArguments()[0];
				if (JsonParsableObject.class.isAssignableFrom(c)) {
					List list = (List) field.getType().newInstance();
					JSONArray jsonArray = jsonObject.getJSONArray(key);
					for(int i=0;i<jsonArray.length();i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						JsonParsableObject d = (JsonParsableObject) c.newInstance();
						d.parse(jsonObj);
						list.add(d);
					}
					field.set(this, list);
				}
			} else {
				Type t = field.getGenericType();
				Class<?> c = (Class<?>) t;
				if (JsonParsableObject.class.isAssignableFrom(c)) {
					JSONObject jsonObj = jsonObject.getJSONObject(key);
					JsonParsableObject d = (JsonParsableObject) c.newInstance();
					d.parse(jsonObj);
					field.set(this, d);
				}
			}

		}
	}
}
