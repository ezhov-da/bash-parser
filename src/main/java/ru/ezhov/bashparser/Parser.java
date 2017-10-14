/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ezhov.bashparser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.ezhov.bashparser.properties.Settings;

/**
 *
 * @author rrndeonisiusezh
 */
public class Parser
{

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());
    /** все пишем в один файл */
    public static final int WRITE_ONE_FILE = 0;
    /** каждую страницу пишем в отдельный файл */
    public static final int WRITE_MANY_FILE = 1;
    /** чтение только одной страницы в общий файл */
    public static final int WRITE_ONE_PAGE = 2;
    /** страница с котрой необходимо парсить */
    private static int pageFrom;
    /** страница по которую нужно парсить включительно */
    private static int pageTo;
    /** место для сохранения файлов */
    private static String directory;
    /** вебсайт */
    private static String website;
    /** класс из которого необходимо брать данные */
    private static String clazz;
    /** корень xml */
    private static String rootXml;
    /** узел xml */
    private static String node;
    /** название общего файла */
    private static String nameCommonFile;
    /** класс парсера */
    private static final Parser parser = new Parser();
    /** фабрика для записи xml */
    private final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    /** записывающий xml объект */
    private DocumentBuilder docBuilder;
    /** документ xml */
    private org.w3c.dom.Document docXml;
    /** записываем xml */
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private Parser()
    {

    }

    public static void parse(int howWrite) throws IOException, TransformerException, ParserConfigurationException
    {
        //инициализируем поля
        parser.initField();
        switch (howWrite)
        {
            case WRITE_ONE_FILE:
                parser.readAndWriteOneFile();
                break;
            case WRITE_MANY_FILE:
                parser.readAndWrite();
                break;
            case WRITE_ONE_PAGE:
                parser.readAndWriteOnePage();
                break;
            default:
                throw new IllegalArgumentException("неверный параметр");
        }

    }

    /**
     * инициализируем поля
     *
     * @throws IOException
     */
    private void initField() throws IOException
    {
        pageFrom = Integer.parseInt(Settings.getProperty(Constants.PAGE_FROM));
        pageTo = Integer.parseInt(Settings.getProperty(Constants.PAGE_TO));
        directory = Settings.getProperty(Constants.DIRECTORY);
        website = Settings.getProperty(Constants.WEBSITE);
        clazz = Settings.getProperty(Constants.CLAZZ);
        rootXml = Settings.getProperty(Constants.ROOT_XML);
        node = Settings.getProperty(Constants.NODE_XML);
        nameCommonFile = Settings.getProperty(Constants.NAME_COMMON_FILE);
    }

    /**
     * читаем и пишим данные в разные файлы
     */
    private void readAndWrite() throws TransformerException, IOException, ParserConfigurationException
    {

        //определяем какие страницы читать
        if (pageTo == -1)
        { //если страница по = -1, тогда читаем только одну страницу
            pageTo = pageFrom;
        }

        docBuilder = docFactory.newDocumentBuilder();//создали записывающий объект
        docXml = docBuilder.newDocument(); //создали документ
        //начинаем перебор страниц
        for (int i = pageFrom; i <= pageTo; i++)
        {

            //получаем страницу
            Document doc = Jsoup.connect(website + i).get();
            //получаем элементы
            Elements elements = doc.select(clazz);
            if (!elements.isEmpty()) //если список элементов не пустой
            {
                docXml = docBuilder.newDocument(); //создали документ

                org.w3c.dom.Element eRootXml = docXml.createElement(rootXml); //создали основной корень
                docXml.appendChild(eRootXml);   //добавляем корен в документ
                //проходимся циклом по элементам
                for (Element element : elements)
                {
                    org.w3c.dom.Element eNodeXml = docXml.createElement(node); //создали узел
                    eNodeXml.appendChild(docXml.createTextNode(element.html())); //добавили текст в узел
                    eRootXml.appendChild(eNodeXml); //добавили узел в корень

                }
                write(i); //записываем файл
            }

        }

    }

    /**
     * читаем и пишим данные в один файл одну страницу
     */
    private void readAndWriteOnePage() throws TransformerException, IOException, ParserConfigurationException
    {

        docBuilder = docFactory.newDocumentBuilder();//создали записывающий объект
        docXml = docBuilder.newDocument(); //создали документ

        //получаем страницу
        Document doc = Jsoup.connect(website).get();
        //получаем элементы
        Elements elements = doc.select(clazz);
        if (!elements.isEmpty()) //если список элементов не пустой
        {
            docXml = docBuilder.newDocument(); //создали документ

            org.w3c.dom.Element eRootXml = docXml.createElement(rootXml); //создали основной корень
            docXml.appendChild(eRootXml);   //добавляем корен в документ
            //проходимся циклом по элементам
            for (Element element : elements)
            {
                org.w3c.dom.Element eNodeXml = docXml.createElement(node); //создали узел
                eNodeXml.appendChild(docXml.createTextNode(element.html())); //добавили текст в узел
                eRootXml.appendChild(eNodeXml); //добавили узел в корень

            }
            write(nameCommonFile); //записываем файл
        }

    }

    /**
     * читаем и пишим данные в один файл
     */
    private void readAndWriteOneFile() throws TransformerException, IOException, ParserConfigurationException
    {

        //определяем какие страницы читать
        if (pageTo == -1)
        { //если страница по = -1, тогда читаем только одну страницу
            pageTo = pageFrom;
        }

        docBuilder = docFactory.newDocumentBuilder();//создали записывающий объект
        docXml = docBuilder.newDocument(); //создали документ
        docXml = docBuilder.newDocument(); //создали документ

        org.w3c.dom.Element eRootXml = docXml.createElement(rootXml); //создали основной корень
        docXml.appendChild(eRootXml);   //добавляем корен в документ        
        //начинаем перебор страниц
        for (int i = pageFrom; i <= pageTo; i++)
        {

            LOG.log(Level.INFO, "парсим {0} страницу", i);
            //получаем страницу
            Document doc = Jsoup.connect(website + i).get();
            //получаем элементы
            Elements elements = doc.select(clazz);
            if (!elements.isEmpty()) //если список элементов не пустой
            {
                //проходимся циклом по элементам
                for (Element element : elements)
                {
                    org.w3c.dom.Element eNodeXml = docXml.createElement(node); //создали узел
                    eNodeXml.appendChild(docXml.createTextNode(element.html())); //добавили текст в узел
                    eRootXml.appendChild(eNodeXml); //добавили узел в корень

                }

            }

        }
        write(nameCommonFile); //записываем файл

    }

    /**
     * записываем файл xml
     */
    private void write(Object pageWrite) throws TransformerConfigurationException, TransformerException
    {
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(docXml);
        StreamResult result = new StreamResult(directory + File.separator + pageWrite + ".xml");
        transformer.transform(source, result);
        LOG.log(Level.INFO, "записана страница: {0}", pageWrite);
    }

    /** класс, который содержит константы свойств */
    private static class Constants
    {

        /** страница с */
        public static final String PAGE_FROM = "pageFrom";
        /** страница по */
        public static final String PAGE_TO = "pageTo";
        /** папка для сохранения */
        public static final String DIRECTORY = "directory";
        /** сайт */
        public static final String WEBSITE = "website";
        /** класс для получения данных */
        public static final String CLAZZ = "class";
        /** корень xml */
        public static final String ROOT_XML = "rootXml";
        /** узед xml */
        public static final String NODE_XML = "node";
        /** название общего файла */
        public static final String NAME_COMMON_FILE = "nameCommonFile";
    }
}
