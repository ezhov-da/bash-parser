package ru.ezhov.demotivatorparser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.ezhov.bashparser.properties.IniFile;

/**
 * класс, который парсит сайт с демотиваторами
 *
 * @author rrndeonisiusezh
 */
public class DemParser
{

    private static final Logger LOG = Logger.getLogger(DemParser.class.getName());

    /** название секции для парсинга из ini */
    private static final String strNameSection = "demotivators";
    private int pageFrom;
    private int pageTo;
    private String linksPath;
    private String website;
    private String userAgent;
    private String docSelect;
    private String img;
    private String src;
    private String pathToPictures;
    private String linksFile;
    private int timeout;

    private DemParser()
    {

    }

    /*объект класса для парсинга*/
    private static final DemParser demParser = new DemParser();

    public static void parse()
    {
        demParser.initProperties(); //получаем настройки
        demParser.writeFileLink();  //начинаем парсинг
    }

    /** читаем и пишим файл с ссылками */
    private void writeFileLink()
    {
        PrintWriter printWriter = null;
        try
        {
            printWriter = new PrintWriter(new File(linksPath));
            for (int i = pageFrom; i <= pageTo; i++)    //начинаем перебирать страницы
            {

                //получаем документ
                Document document = Jsoup.connect(website + i)
                        .userAgent(userAgent)
                        .get();

                Elements elements = document.select(docSelect);
                elements = elements.select(img);
                int size = elements.size();
                for (int el = 0; el < elements.size(); el++)    //перебираем элементы
                {

                    String url = elements.get(el).absUrl(src); //получаем абсолютный путь
                    url = url.replaceFirst(".thumbnail.", "."); //заменяем фразу в ссылке, так как нам нужна картинка нормального размера
                    printWriter.write(i + "_" + el + "=" + url + "\n");  //пишем файл

                }
                LOG.log(Level.INFO, "записали ссылки со страницы: {0}", i);

            }
            printWriter.flush();
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        } finally
        {
            if (printWriter != null)
            {
                printWriter.close();
            }
        }
    }

    /**
     * сохраняем картинки
     */
    public static void savePictures()
    {
        demParser.savePicProp();
    }

    /**
     * метод сохранения картинок как обычные ссылки, не как файл настроек
     */
    @Deprecated
    private void savePic()
    {
        LOG.info("начали скачивание картинок");
        demParser.initProperties(); //получаем настройки
        URL url;
        String strLink;
        int counter = 0;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try
        {
            Scanner scanner = new Scanner(new File(linksFile)); //получаем чтеца файла
            while (scanner.hasNextLine())   //перебираем ссылки
            {
                try
                {
                    strLink = scanner.nextLine();  //получаем ссылку
                    url = new URL(strLink); //получаем адресс
                    URLConnection connection = url.openConnection();
                    connection.addRequestProperty("User-Agent", userAgent);
                    connection.connect();
                    bufferedInputStream = new BufferedInputStream(connection.getInputStream());
                    fileOutputStream = new FileOutputStream(new File(pathToPictures + File.separatorChar + counter + ".jpg"));
                    byte[] buffer = new byte[4096];
                    int byteRead;
                    LOG.log(Level.INFO, "начали скачивание картинки: {0}", counter);
                    while ((byteRead = bufferedInputStream.read(buffer)) != -1)
                    {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    LOG.info("скачали картинку");
                    counter++;
                } finally
                {
                    if (bufferedInputStream != null)
                    {
                        bufferedInputStream.close();
                    }
                    if (fileOutputStream != null)
                    {
                        fileOutputStream.close();
                    }
                }

            }

        } catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "не удалось получить ссылку", ex);
        }

    }

    private void savePicProp()
    {
        LOG.info("начали скачивание картинок");
        demParser.initProperties(); //получаем настройки
        URL url;
        String strLink;
        int counter = 0;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try
        {
            new File(pathToPictures).mkdirs(); //создаем папку для хранения файлов
            Properties properties = new Properties();   //данные с ссылками
            properties.load(new FileInputStream(new File(linksFile)));  //загружаем файл с ссылками
            Enumeration<Object> enumeration = properties.keys();    // получаем список ключей
            while (enumeration.hasMoreElements())   //перебираем ключи
            {
                try
                {
                    Object objectKey = enumeration.nextElement(); //получаем ключ
                    strLink = properties.get(objectKey).toString();  //получаем ссылку
                    url = new URL(strLink); //получаем адресс
                    URLConnection connection = url.openConnection(); //создаем подключение
                    connection.addRequestProperty("User-Agent", userAgent); //ставим настройки
                    connection.connect();   //подключаемся
                    bufferedInputStream = new BufferedInputStream(connection.getInputStream()); //получаем входной поток
                    fileOutputStream = new FileOutputStream(new File(pathToPictures + File.separatorChar + objectKey + ".jpg"));    //получаем файл на выход
                    byte[] buffer = new byte[4096]; //массив для буфера
                    int byteRead;
                    LOG.log(Level.INFO, "начали скачивание картинки: {0}", counter);
                    while ((byteRead = bufferedInputStream.read(buffer)) != -1) //пишем файл
                    {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    LOG.info("скачали картинку");
                    counter++;  //прибавляем счетчик файла
                    Thread.sleep(timeout);  //ждем для нового подключения
                } finally
                {
                    if (bufferedInputStream != null)
                    {
                        bufferedInputStream.close();
                    }
                    if (fileOutputStream != null)
                    {
                        fileOutputStream.close();
                    }
                }

            }

        } catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "не удалось получить ссылку", ex);
        }

    }

    private void initProperties()
    {
        pageFrom = Integer.parseInt(IniFile.getProperties(strNameSection, "pageFrom"));
        pageTo = Integer.parseInt(IniFile.getProperties(strNameSection, "pageTo"));
        if (pageTo == -1)
        {
            pageTo = pageFrom;    //если страница по = -1, тогда страница по = страница с
        }
        linksPath = IniFile.getProperties(strNameSection, "linksPath");
        website = IniFile.getProperties(strNameSection, "website");
        userAgent = IniFile.getProperties(strNameSection, "userAgent");
        docSelect = IniFile.getProperties(strNameSection, "document.select");
        img = IniFile.getProperties(strNameSection, "elements.select");
        src = IniFile.getProperties(strNameSection, "absUrl");
        pathToPictures = IniFile.getProperties(strNameSection, "pathToPictures");
        linksFile = IniFile.getProperties(strNameSection, "linksFile");
        timeout = Integer.parseInt(IniFile.getProperties(strNameSection, "timeout"));
    }
}
