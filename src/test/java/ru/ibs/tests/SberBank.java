package ru.ibs.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class SberBank {
    WebDriver driver;
    WebDriverWait wait;


    @Before
    public void before() {
        // Вызываем дравейвер, который нам открывает хром
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe"); // прописывается в каждом проекте
        driver = new ChromeDriver();

        // Растягиваем окно на полный экран
        driver.manage().window().maximize();

        // неявные ожидания, один раз задаю и везде работают одинаково
        // pageLoadTimeout(10, TimeUnit.SECONDS) Будет в течении 10 секунд ждать загрузку страницы, как
        // только загружается он отрубается и не мешает
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);   // Ожидает, когда страничка будет загружена
        // если нужен таймаут для js скриптов, driver можно привести к виду JavascriptExecutor
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        // Везде где вызывается findElement начинается ожидание получения элемента заданное время
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Явные ожидания
        // Если где то будет использоваться объект wait, то будем ожидать условие в течении 10 секунд с интервалом 2 секунды
        // То есть браузер будет опрошен всего 5 раз, если условие не возникло ,которое ожидали, то упадет timeOutExeption
        wait = new WebDriverWait(driver, 10, 2000);


        // Переход на сайт
        driver.get("https://www.sberbank.ru/ru/person");    // Не сохраняет переход в браузере
    }

    @Test
    public void test() {

        // тут падала ошибка раньше из-за того ,что страница не успевала загрузиться

        // Убераю кнопку региона
        By by = By.xpath("//button[text()='Да' and contains(@class, 'kitt-region__accept') and contains(@aria-label, 'Да, оставить регион 77')]");

        // Проверка на присутствие элемента, если есть, то делаем действие, если нет, то идем дальше
        if (elementIsExist(by)) {
            WebElement region = driver.findElement(by);
            region.click();
        }

        // Выделяем нужный элемент
        WebElement baseMenu = driver.findElement(By.xpath("//a[contains(@aria-label, 'Страхование') and contains(@role, 'button')]"));
        // Самым стабильным вариантом является вырывание по тегу с указанием его атрибутов

        // Кликнем по элементу
        baseMenu.click();

        // Проверяем, что был совершен клик
        WebElement parentBaseMenu = baseMenu.findElement(By.xpath("./.."));

        // Вызываем ожидание. Всегда вызывается фунекциоя until, которая позволяет что то ожидать в цикле, который ограничен временем в wait
        // внутрь нужен элемент из класса ExpectedConditions
        // Заменил проверку снизу на эту так, что теперь буду ждать ,когда появится атрибут класс со значением opened
        wait.until(ExpectedConditions.attributeContains(parentBaseMenu, "class", "opened"));

        // менее желательная проверка, чтобы
        // getAttribute возращает, что внутри атрибута лежит, а контейнсом дальше проверяем содержится ли текст, который вывел гетАтрибут
        Assert.assertTrue("Клик по страхованию не был совершен", parentBaseMenu.getAttribute("class").contains("opened"));

        // Переходим в следующую вкладку
        WebElement subMenu = driver.findElement(By.xpath("//a[text()='Путешествия' and contains(@href, 'travel')]"));
        subMenu.click();

        WebElement titleTravelPage = driver.findElement(By.xpath("//h1[@data-test-id='PageTeaserDict_header']"));

        // Дожидаемся пока отрисуется элемент. То есть дожидаемся отрисовки определенного состояния элемента
        wait.until(ExpectedConditions.visibilityOf(titleTravelPage));

        // Теперь делаем проверку
        // Эта штука делает проверку и если второй элемент true, то он проходит дальше, если false, то он выкидывает
        // то, что в первом аргументе, то есть в моем кейсе "Страничка не загрузилась"
        // первая проверка - загрузиласль ли страничка
        Assert.assertTrue("Страничка не загрузилась", titleTravelPage.isDisplayed());

        // вторая проверка - совпал ли текст
        // Ассерт иквалс сам выводит эекспектед и эктуал
        Assert.assertEquals("Страхование путешественников", titleTravelPage.getText());

        // Поставим точку остановки
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


/**        WebElement baseMenu = driver.findElement(By.id("main-page"));    // Найти элемен у которого задан атрибут или свойство id
 // атрибут это все, что указано после тега, внутри него <a .... атрибуты.... ></a>
 // по айди лучше всего, круче всего
 WebElement baseMenu = driver.findElement(By.name("twitter:card"));  // Поиск по атрибуду name
 WebElement baseMenu = driver.findElement(By.tagName("a"));      // Поиск по тегу, то есть вернёт первый встреченный элемент по тегу
 WebElement baseMenu = driver.findElement(By.linkText("Перейти к основному элементу")); // Ищем по тексту в теге
 WebElement baseMenu = driver.findElement(By.partialLinkText("вному элементу"));  // Искать по чати текста, как contains в жабе
 WebElement baseMenu = driver.findElement(By.className(""));   // Поиск по тегу, айфрейм, скрипт, используется редко
 WebElement baseMenu = driver.findElement(By.cssSelector(".bc-item__title")); // Основан на языке CSS. Отдельная темная тема

 // Надо знать хорошо, работает как файловая система
 // DOM document object model - объектная модель документа
 // XML прагает на этот элемент как по папкам
 // Провалиться в раздел /DDD
 // Рекурсивно провалиться во все элементы подходящие //DDD
 // /@id собачка означает, что надо найти по атрибуту
 // Комбинируем значения имени тега и его атрибута //BBB[@id] в квадратных скобках указываются только атрибуты
 // //BBB[@id] ищет все разделы bbb где есть атрибуты id
 // //BBB[@*] вырывает все элементы у которых есть атрибуты
 // //BBB[not(@*)] вырывает все элементы у которых нет атрибутов вообще
 // //BBB[@id='bbb'] вырываем элементы, у которых атрибут name = bbb

 // В Xpath есть такое понятие как оси. Они не всегда нужны, так что использовать аккуратно
 // Элементы, которые мы берем называют еще нодами, от которых мы можем идти вверх/вниз и т.д.
 // Ось //BBB/ancestor::* вырывание всех предков данного тега, то есть вырывает все вышестоящие блоки
 WebElement baseMenu = driver.findElement(By.xpath(""));

 baseMenu.click();   // Кликнем по элементу


 driver.navigate().to("https://www.sberbank.ru/ru/person");  // Та же запись, что в выше, сохраняет историю переходов в браузере
 driver.navigate().back();   // Если использовать запись с историей, то можно использовать back, который будет возвращать на
 // предыдущую страницу если был переход на другую страницу. Если будет клик по элементу, то back и его откатит
 // как правило хватает и обычного get, т.к. тест происходит линейно
 driver.navigate().forward();    // работает как back, только переходит на страницу вперед
 driver.navigate().refresh();    // обновляет страницу

 driver.manage().addCookie();    // Добавить куки в браузере
 driver.manage().deleteAllCookies(); // Удалить все куки
 driver.manage().deleteCookieNamed();    // удалить конкретную куки

 driver.manage().window().fullscreen();  // Установить фулскрин окна. Кнопочка расширить
 driver.manage().window().maximize();    // Расширить на все окно
 driver.manage().window().getPosition(); // Вернуть конкретную позицию браузера, с какого пикселя по X, Y он начинается

 driver.getTitle();   // Получение значения элемента, который хранится в хедере - <title>

 driver.findElements();  // Находит все элементы по конретному пути и возвращает лист элементов

 driver.getCurrentUrl(); // Возвращает url, который сейчас забит

 driver.getWindowHandle();   // Возвращает айдишник активного окна, который сейчас открыт, селениум сам устанавливает айдишники каждому окну
 driver.getWindowHandles();  // Возвращает айдишники вкладочек, которые открыты в данном окне


 // переключиться на что то
 driver.switchTo().window();  // Перейти в нужное окошко
 driver.switchTo().activeElement();  // Возвращает активный веб элемент при обновлении странички

 driver.switchTo().alert();  // Если всплывает окошко какое то
 driver.switchTo().alert().accept(); // Разрешает аллерту делать что хочет
 driver.switchTo().alert().dismiss();    // Не разрешает ему делать то, что он хочет

 driver.switchTo().frame();  // Устаревшая технология если надо переключиться на что то на какие то фреймы
 // фрейм это страничка в страничке со своими хедерами бади и т.д.
 // Чтобы взаимодействовать с этими фреймами надо сначала переключиться на этот фрейм, но чтобы обратиться к
 // элементам не во фрейме, то придется опять переключаться из фрейма в обратно
 driver.switchTo().parentFrame();    // переключиться на родительский фрейм

 driver.getPageSource();     // возращает html ку страницы, иногда полезно если есть хитрые страницы, которые,
 // например, не переключаются на страницу


 // Закрытие браузера в конце теста
 driver.quit();  // Закрытие браузера и схлопывание сессии
 driver.close(); // Закрытие конкретно вкладочки, но не закрытие сессии
 */
    }

    // Проверка на появление элемента на странице. Передаю элемент by, то есть локатор
    public boolean elementIsExist(By by){
        try {
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.findElement(by);
            return true;
            // Перехватываю ошибку от Selenium, не от juva.util
        } catch (NoSuchElementException ignore) {}
        finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return false;   // запись вне кетча дает вернуть фолс в любом случа, а ошибку игнорим
    }

    // Выполнится после выполнения кейса, то есть в любом случае, даже если он зафейлится, то браузер теперь закроется
    @After
    public void after() {
        // Закрытие браузера в конце теста
        driver.quit();
    }
}
