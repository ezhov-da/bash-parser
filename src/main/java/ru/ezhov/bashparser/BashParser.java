package ru.ezhov.bashparser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author rrndeonisiusezh
 */
public class BashParser
{

    private static final Logger LOG = Logger.getLogger(BashParser.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            long startTime = System.currentTimeMillis();

            LOG.info("начинаем парсинг");
            Parser.parse(Parser.WRITE_MANY_FILE);
            long endTime = System.currentTimeMillis();

            LOG.log(Level.INFO, "парсинг завершен за {0} секунд(ы)", (endTime - startTime) / 1000);
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

}
