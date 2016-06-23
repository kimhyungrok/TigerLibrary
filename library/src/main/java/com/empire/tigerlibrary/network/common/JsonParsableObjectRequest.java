package com.empire.tigerlibrary.network.common;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

public class JsonParsableObjectRequest extends Request<JsonParsableObject> {

    public static final String TYPE_MIME_FILE_PNG = "image/png";
    public static final String TYPE_MIME_FILE_GIF = "image/gif";
    public static final String TYPE_MIME_FILE_JPG = "image/jpg";

    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_JSON_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);

    private boolean mIsJsonRequestBody;
    private boolean mIsMultiPartBody;

    private HttpEntity mHttpEntity;
    private File mFilePart;
    private Map<String, String> mStringPart;
    private String mRequestBody;
    private Listener<JsonParsableObject> mListener;
    private JsonParsableObject mParsableObject;

    private JsonParsableObjectRequest(int method, String url, JSONObject jsonRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        super(method, url, errorListener);
        mIsJsonRequestBody = true;
        mRequestBody = jsonRequest.toString();
        mListener = listener;
        mParsableObject = parsableObject;
    }

    private JsonParsableObjectRequest(int method, String url, String strRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        super(method, url, errorListener);
        mIsJsonRequestBody = false;
        mRequestBody = strRequest;
        mListener = listener;
        mParsableObject = parsableObject;
    }

    private JsonParsableObjectRequest(int method, String url, Map<String, String> params, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        super(method, url, errorListener);
        mIsJsonRequestBody = false;
        mListener = listener;
        mParsableObject = parsableObject;

        try {
            if (params != null && params.size() > 0) {
                StringBuilder encodedParams = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), PROTOCOL_CHARSET));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), PROTOCOL_CHARSET));
                    encodedParams.append('&');
                }
                mRequestBody = encodedParams.toString();
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding : %s", e.toString());
        }

    }

    public JsonParsableObjectRequest(String url, JSONObject jsonRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest, listener, errorListener, parsableObject);
    }

    public JsonParsableObjectRequest(String url, String strRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        this(strRequest == null ? Method.GET : Method.POST, url, strRequest, listener, errorListener, parsableObject);
    }

    public JsonParsableObjectRequest(String url, Map<String, String> params, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        this(params == null ? Method.GET : Method.POST, url, params, listener, errorListener, parsableObject);
    }

    public JsonParsableObjectRequest(String url, MapParsableObject mapRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        this(mapRequest == null ? Method.GET : Method.POST, url, mapRequest.makeMapObject(), listener, errorListener, parsableObject);
    }

    public JsonParsableObjectRequest(String url, JsonParsableObject jsonRequest, Listener<JsonParsableObject> listener, ErrorListener errorListener,
            JsonParsableObject parsableObject) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest.makeJSONObject(), listener, errorListener, parsableObject);
    }

    public JsonParsableObjectRequest(String url, Listener<JsonParsableObject> listener, ErrorListener errorListener, JsonParsableObject parsableObject) {
        super(Method.GET, url, errorListener);
        mIsJsonRequestBody = false;
        mListener = listener;
        mParsableObject = parsableObject;
    }

    public JsonParsableObjectRequest(String url, Map<String, String> stringPart, String filePartName, File filePart, String fileMimeType,
            Listener<JsonParsableObject> listener, ErrorListener errorListener, JsonParsableObject parsableObject) {
        super(Method.POST, url, errorListener);
        mIsMultiPartBody = true;
        mListener = listener;
        mParsableObject = parsableObject;
        mFilePart = filePart;
        mStringPart = stringPart;

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setCharset(Charset.forName(PROTOCOL_CHARSET));
        if (mFilePart != null) {
            entity.addPart(filePartName, new FileBody(mFilePart, ContentType.create(fileMimeType), mFilePart.getName()));
        }
        if (mStringPart != null) {
            for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
                entity.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
            }
        }
        mHttpEntity = entity.build();
    }

    @Override
    protected void deliverResponse(JsonParsableObject response) {
        try {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        try {
            ErrorListener errorListener = getErrorListener();
            if (errorListener != null) {
                errorListener.onErrorResponse(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Response<JsonParsableObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            mParsableObject.parse(new JSONObject(jsonString));
            return (Response<JsonParsableObject>) Response.success(mParsableObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (IllegalArgumentException e) {
            return Response.error(new ParseError(e));
        } catch (NoSuchFieldException e) {
            return Response.error(new ParseError(e));
        } catch (IllegalAccessException e) {
            return Response.error(new ParseError(e));
        } catch (InstantiationException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        String bodyContentType = null;
        if (mIsJsonRequestBody) {
            bodyContentType = PROTOCOL_JSON_CONTENT_TYPE;
        } else if (mIsMultiPartBody) {
            bodyContentType = mHttpEntity.getContentType().getValue();
        } else {
            bodyContentType = PROTOCOL_CONTENT_TYPE;
        }
        return bodyContentType;
    }

    @Override
    public byte[] getBody() {
        byte[] body = null;
        try {
            if (mIsJsonRequestBody) {
                body = mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
            } else if (mIsMultiPartBody) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mHttpEntity.writeTo(bos);
                body = bos.toByteArray();
            } else {
                body = mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
            }
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

}
