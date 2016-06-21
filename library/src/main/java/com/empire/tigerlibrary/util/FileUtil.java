package com.empire.tigerlibrary.util;

import android.database.Cursor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.security.InvalidParameterException;

/**
 * useful class composed by methods for file operation
 *
 * @author lordvader
 */
public class FileUtil {
    /**
     * save Object to File
     *
     * @param object
     * @param fileName
     */
    public static void saveObjectToFile(Object object, String fileName) {
        ObjectOutputStream output = null;

        try {
            if (object == null || isEmptyString(fileName)) {
                throw new InvalidParameterException("invalid parameter");
            }

            output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            output.writeObject(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        } finally {
            closeSilently(output);
        }
    }

    /**
     * load Object from file
     *
     * @param fileName
     * @return
     */
    public static Object loadObjectFromFile(String fileName) {
        ObjectInputStream input = null;
        Object object = null;

        try {
            input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            object = input.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeSilently(input);
        }
        return object;
    }

    /**
     * close stream and connection object silently by catching exception bySelf
     *
     * @param objects
     */
    public static void closeSilently(Object... objects) {
        if (objects != null) {
            for (Object object : objects) {
                if (object != null) {
                    try {
                        if (object instanceof InputStream) {
                            ((InputStream) object).close();
                        } else if (object instanceof OutputStream) {
                            ((OutputStream) object).close();
                        } else if (object instanceof Cursor) {
                            ((Cursor) object).close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * copy source file to target path
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void copy(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            if (sourceFile == null || sourceFile.length() == 0) {
                throw new InvalidParameterException("invalid parameter");
            }

            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bos = new BufferedOutputStream(new FileOutputStream(targetPath));

            int readLength;
            byte[] buffer = new byte[1024];

            while ((readLength = bis.read(buffer)) > -1) {
                bos.write(buffer, 0, readLength);
            }

            bos.flush();
        } catch (FileNotFoundException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        } finally {
            closeSilently(bis, bos);
        }
    }

    /**
     * delete file
     *
     * @param path
     * @return
     */

    public static boolean deleteFile(String path) {
        File deleteFile = new File(path);
        if (deleteFile != null && deleteFile.exists()) {
            return deleteFile.delete();
        }

        return true;
    }

    /**
     * get file bytes array
     *
     * @param file
     * @return
     */
    public static byte[] getBytesArray(File file) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (file == null || !file.exists()) {
            return buffer.toByteArray();
        }
        byte[] data = new byte[4096];
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(inputStream);
        }

        return buffer.toByteArray();
    }

    /**
     * convert byteArray to files
     *
     * @param fileName
     * @param bytes
     * @return
     * @throws IOException
     */
    public static File byteArrayToFile(String fileName, byte[] bytes) {
        File file = new File(fileName);
        FileOutputStream fos = null;

        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fos);
        }
        return file;
    }

    /**
     * make directory
     *
     * @param dirPath
     */
    public static void makeDirs(String dirPath) {
        if (isEmptyString(dirPath)) {
            return;
        }

        File dir = new File(dirPath);
        dir.mkdirs();
    }

    /**
     * check whether string is null or empty
     *
     * @param str
     * @return
     */
    public static boolean isEmptyString(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }
}
