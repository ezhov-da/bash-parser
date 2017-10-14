package ru.ezhov.bashparser.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ezhov_da
 */
public final class Settings
{

    private Settings()
    {
    }
    /** файл с настройками приложения */
    private final static String nameIninFile = "bash.ini";
    /** класс с настройками, считывается каждый раз при обращении */
    private final static Properties properties = new Properties();
    private static FileInputStream inputStream;

    /**
     * получаем свойство по ключу
     * <p>
     * @param key - ключ свойства
     * <p>
     * @return свойство
     * <p>
     * @throws java.io.IOException
     */
    public static final synchronized String getProperty(String key) throws IOException
    {
        loadProperties();
        return properties.getProperty(key);
    }

    /**
     * возвращаем класс с подгруженными настройками
     * <p>
     * @return настройки
     * <p>
     * @throws java.io.IOException
     */
    public static synchronized Properties getProperties() throws IOException
    {
        loadProperties();
        return properties;
    }

    /**
     * загружаем настройки
     * <p>
     * @throws IOException
     */
    private static final void loadProperties() throws IOException
    {
        try
        {
            inputStream = new FileInputStream(new File(nameIninFile));
            properties.load(inputStream);
        } finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }
}
