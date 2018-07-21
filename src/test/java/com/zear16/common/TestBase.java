package com.zear16.common;

import com.zear16.common.util.Convert;
import org.apache.http.cookie.Cookie;
import org.doctester.DocTester;
import org.doctester.rendermachine.RenderMachine;
import org.doctester.rendermachine.RenderMachineCommands;
import org.doctester.rendermachine.RenderMachineImpl;
import org.doctester.testbrowser.*;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class TestBase implements TestBrowser, RenderMachineCommands {

    @Rule
    public TestRule testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            classNameForDocTesterOutputFile = description.getClassName();
        }
    };

    /**
     * classNameForDocTesterOutputFile will be set by the testWatcher. That way we
     * can easily generate a filename as output filename. Usually it is something
     * like "com.mycompany.NameOfClassTest".
     */
    private String classNameForDocTesterOutputFile;

    private final Logger logger = LoggerFactory.getLogger(DocTester.class);

    // Unique for each test method.
    private TestBrowser testBrowser;

    // Unique for whole testClass => one outputfile per testClass.
    private static RenderMachine renderMachine = null;

    @Before
    public void setupForTestCaseMethod() {

        initRenderingMachineIfNull();

        // Set a fresh TestBrowser for each testmethod.
        testBrowser = getTestBrowser();
        renderMachine.setTestBrowser(testBrowser);

        // This is all a bit strange. But JUnit's @BeforeClass
        // is static. Therefore the only possibility to transmit
        // the filename to the renderMachine is here.
        // We accept that we set the fileName too often.
        renderMachine.setFileName(classNameForDocTesterOutputFile);

    }

    public void initRenderingMachineIfNull() {

        if (renderMachine == null) {
            renderMachine = getRenderMachine();
        }

    }

    @AfterClass
    public static void finishDocTest() {

        if (renderMachine != null) {
            renderMachine.finishAndWriteOut();
            renderMachine = null;
        }

    }

    // ////////////////////////////////////////////////////////////////////////
    // Say methods to print stuff into html
    // ////////////////////////////////////////////////////////////////////////
    @Override
    public final void say(String textAsParagraph) {
        renderMachine.say(textAsParagraph);
    }

    @Override
    public final void sayNextSection(String textAsH1) {
        renderMachine.sayNextSection(textAsH1);
    }

    @Override
    public final void sayRaw(String rawHtml) {
        renderMachine.sayRaw(rawHtml);
    }

    @Override
    public final <T> void sayAndAssertThat(String message,
                                           String reason,
                                           T actual,
                                           Matcher<? super T> matcher) {

        renderMachine.sayAndAssertThat(message, reason, actual, matcher);

    }

    @Override
    public final <T> void sayAndAssertThat(String message,
                                           T actual,
                                           Matcher<? super T> matcher) {

        sayAndAssertThat(message, "", actual, matcher);

    }

    // //////////////////////////////////////////////////////////////////////////
    // Inlined methods of the TestBrowser (for convenience)
    // //////////////////////////////////////////////////////////////////////////
    /**
     * @return all cookies saved by this TestBrowser.
     */
    @Override
    public final List<Cookie> getCookies() {
        return testBrowser.getCookies();
    }

    @Override
    public final List<Cookie> sayAndGetCookies() {
        return testBrowser.getCookies();
    }

    @Override
    public final Cookie getCookieWithName(String name) {
        return testBrowser.getCookieWithName(name);
    }

    @Override
    public final Cookie sayAndGetCookieWithName(String name) {
        return testBrowser.getCookieWithName(name);
    }

    @Override
    public final void clearCookies() {
        testBrowser.clearCookies();
    }

    @Override
    public final Response makeRequest(Request httpRequest) {
        return testBrowser.makeRequest(httpRequest);
    }

    @Override
    public final Response sayAndMakeRequest(Request httpRequest) {
        return renderMachine.sayAndMakeRequest(httpRequest);
    }

    // //////////////////////////////////////////////////////////////////////////
    // Configuration of DoctestJ
    // //////////////////////////////////////////////////////////////////////////
    /**
     * You may override this method if you want to supply your own testbrowser for
     * your class or classes.
     *
     * @return a TestBrowser that will be used for each test method.
     */
    public TestBrowser getTestBrowser() {

        return new TestBrowserImpl();

    }

    /**
     * You may override this method if you want to supply your own rendering
     * machine for your class or classes.
     *
     * @return a RenderMachine that generates output and lives for a whole test
     * class.
     */
    public RenderMachine getRenderMachine() {

        return new RenderMachineImpl();

    }

    /**
     * Convenience method that allows you to write tests with the testbrowser in a
     * fluent way.
     *
     * <code>
     *
     * sayAndMakeRequest(
     *           Request
     *               .GET()
     *               .url(testServerUrl().path("search").addQueryParameter("q", "toys")));
     * </code>
     *
     *
     * @return a valid host name of your test server (eg http://localhost:8127).
     * This will be used in the testServerUrl() method.
     */
    public Url testServerUrl() {

        final String errorText = "If you want to use the TestBrowser you have to override getTestServerUrl().";
        logger.error(errorText);

        throw new IllegalStateException(errorText);
    }

    protected final <T> void sayAssertIs(String message, T actual, T expected) {
        sayAndAssertThat(
                message + " ต้องมีค่าเป็น " + Convert.toString(expected),
                actual,
                equalTo(expected));
    }

    protected final void sayAssertRecordCount(String message, int actual, int expected) {
        sayAndAssertThat(
                message + " จะต้องมีข้อมูลจำนวน " + expected + " รายการ",
                actual,
                equalTo(expected));
    }

    protected final void sayAssertIsNull(String message, Object actual) {
        sayAndAssertThat(
                message + " เป็น NULL",
                actual,
                nullValue());
    }

    protected final void openTestCase(String message, String id) {
        sayRaw(
                "<div>" +
                        "<button type=\"button\" class=\"btn btn-info\"" +
                        " width=\"100%\"" +
                        " data-toggle=\"collapse\"" +
                        " data-target=\"#" + id + "\">" + message +
                        "</button>" +
                        "</div>");
        sayRaw("<div id=\"" + id + "\" class=\"collapse\">");
        sayNextSection(message);
    }

    protected final void saySubSectionPreCondition() {
        saySubSection("การเตรียมข้อมูล");
    }

    protected final void saySubSectionProcess() {
        saySubSection("การทดสอบ");
    }

    protected final void saySubSectionExpectation() {
        saySubSection("ผลที่คาดว่าจะได้รับ");
    }

    protected final void saySubSectionPostProcess() {
        saySubSection("การล้างข้อมูล");
    }

    protected final void closeTestCase() {
        sayRaw("</div>");
    }

    protected final void saySubSection(String message) {
        sayRaw("<h3>" + message + "</h3>");
    }

    protected final void sayStep(String message) {
        sayRaw("<h4>" + message + "</h4>");
    }

    protected final void openPreconditionSection() {
        sayRaw("<div class=\"alert alert-info\">");
    }

    protected final void closePreconditionSection() {
        sayRaw("</div>");
    }

    protected final void openProcessSection() {
        sayRaw("<div class=\"alert alert-info\">");
    }

    protected final void closePostProcessSection() {
        sayRaw("</div>");
    }

    protected final void openPostProcessSection() {
        sayRaw("<div class=\"alert alert-info\">");
    }

    protected final void closeProcessSection() {
        sayRaw("</div>");
    }

    protected final void sayPrecondition(String message) {
        sayRaw(
                "<div class=\"alert alert-info\">" +
                        message +
                        "</div>");
    }

    protected final void sayPostProcess(String message) {
        sayRaw(
                "<div class=\"alert alert-info\">" +
                        message +
                        "</div>");
    }

    protected final void sayProcess(String message) {
        sayRaw(
                "<div class=\"alert alert-info\">" +
                        message +
                        "</div>");
    }

}
