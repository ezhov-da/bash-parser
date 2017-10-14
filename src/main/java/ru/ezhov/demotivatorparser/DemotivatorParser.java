package ru.ezhov.demotivatorparser;

import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ezhov.bashparser.properties.IniFile;

/**
 *
 * @author rrndeonisiusezh
 */
public class DemotivatorParser
{

    private static final int PARSER_LINKS = 0;
    private static final int SAVE_PIC = 1;

    private static final Logger LOG = Logger.getLogger(DemotivatorParser.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int whatDo = SAVE_PIC;   //что делаем
        //----------------------------------------------------------------------
        LOG.info("начали парсинг");
        long startTime = System.currentTimeMillis();
        IniFile.loadIni();
        switch (whatDo)
        {
            case PARSER_LINKS:
                DemParser.parse();    //получаем ссылки с сайта
                break;
            case SAVE_PIC:
                DemParser.savePictures();   //сохраняем картинки
                break;
            default:
                throw new IllegalArgumentException("нет такого действия");
        }
        long endTime = System.currentTimeMillis();
        LOG.log(Level.INFO, "парсинг завершен за {0} секунд(ы)", (endTime - startTime) / 1000);
    }

}
