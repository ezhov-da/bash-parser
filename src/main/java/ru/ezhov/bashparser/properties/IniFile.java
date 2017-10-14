package ru.ezhov.bashparser.properties;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 * Это класс для работы с ini файлом
 *
 * @author rrndeonisiusezh
 */
public class IniFile
{

    private static final String FILE = "parser.ini";
    private static Ini ini; //основной класс для настроек
    private static final Logger LOG = Logger.getLogger(IniFile.class.getName());

    private IniFile()
    {

    }

    public static void loadIni()
    {
        try
        {
            ini = new Ini();
            ini.load(new File(FILE));
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, "не найден файл с настройками: " + FILE, ex);
        }
    }

    public static synchronized String getProperties(String nameSection, String nameProperties)
    {
        Section section = ini.get(nameSection);

        String jdbcUrl = section.get(nameProperties);
        if (jdbcUrl == null)
        {
            try
            {
                throw new Exception("не найдена настройка: " + nameProperties);
            } catch (Exception ex)
            {
                LOG.log(Level.SEVERE, "не найдена настройка", ex);
            }
        }
        return jdbcUrl;
    }
}
