import com.codeborne.selenide.Condition;
import emias.AbstractTestGrid;
import emias.testngRetryCount.RetryCountIfFailed;
import io.qameta.allure.Epic;
import io.qameta.allure.Flaky;
import io.qameta.allure.Issue;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import pages.calldoctor.profiles_interfaces.Pacient;

import java.io.IOException;
import java.text.ParseException;

import static com.codeborne.selenide.Selenide.$;

public class CreateCallTest extends AbstractTestGrid {

    @Test(groups = "VD", description = "Запись на прием к врачу")
    @Epic("Запись на прием")
    @RetryCountIfFailed(2)
    public void testVisitDoctor() throws IOException, InterruptedException, ParseException, JSONException {
        Pacient pacient = new Pacient("Profile0");
        Auth.enterCallCenter();
        Pages.createCallPage().createCall(pacient);
        Pages.RecordDoctorPage().recordDoctor()
        Pages.fullCardPage()
                .verifyNewCall(pacient)
                .closeCardBtn();
    }

    @Test(groups = "VD", description = "вызов с иточником Регистратура без МКАБ")
    @Epic("Запись на прием")
    @RetryCountIfFailed(2)
    public void testCallRegistr() throws Exception {
        Pacient pacient = new Pacient("Profile1");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall(pacient);
        Pages.fullCardPage()
                .verifyNewCall(pacient)
                .closeCardBtn();
        Pages.dashboardPage().verifyNewCallGroup(pacient);
    }

    @Test(groups = "CD", description = "вызов с источником СМП и привязкой МКАБ")
    @Epic("Создание вызова")
    @RetryCountIfFailed(3)
    public void testCallRegistrMkab() throws Exception {
        Pacient pacient = new Pacient("Profile2");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall(pacient);
        Pages.fullCardPage()
                .verifyNewCall(pacient)
                .closeCardBtn();
        Pages.dashboardPage().verifyNewCallGroup(pacient);
    }

    @Test(groups = "CD", description = "вызов от СМП по api, ребенок по МКАБ без КЛАДР")
    @Epic("Создание вызова")
    @RetryCountIfFailed(2)
    public void testCallSmpChildMkab() throws IOException, InterruptedException, JSONException {
        Pacient pacient = new Pacient("Profile3");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall_Api(pacient);
        Pages.dashboardPage().openNewCallDash(pacient);
        Pages.fullCardPage().verifyNewCall(pacient);
    }

    @Test(groups = "CD", description = "вызов от СМП по api, взрослый без МКАБ по КЛАДР")
    @Epic("Создание вызова")
    @RetryCountIfFailed(2)
    public void testCallSmpAdultKladr() throws IOException, InterruptedException, JSONException {
        Pacient pacient = new Pacient("Profile6");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall_Api(pacient);
        Pages.dashboardPage().openNewCallDash(pacient);
        Pages.fullCardPage().verifyNewCall(pacient);
    }

    @Test(groups = "CD", description = "вызов ребенка с Портала")
    @Epic("Создание вызова")
    @RetryCountIfFailed(2)
    public void testCallPortal() throws IOException, InterruptedException, JSONException {
        Pacient pacient = new Pacient("Profile4");
        enterSite.enterPortal();
        Pages.portalDashboard().createCall(pacient);
        enterSite.enterCalldoctorFromMis();
        Pages.dashboardPage()
                .clearAllFilters()
                .openNewCallDash(pacient);
        Pages.fullCardPage().verifyNewCall(pacient);
    }

    @Flaky
    @Test(groups = "CD", description = "вызов из Колл-Центра по api, ребенок по МКАБ без КЛАДР. 2 участка. Проставиться не должен ни один")
    @Epic("Создание вызова")
    @Issue("EMIAS-657")
    @RetryCountIfFailed(2)
    public void testCallCenterChildMkab() throws IOException, InterruptedException, JSONException {
        Pacient pacient = new Pacient("Profile14");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall_Api(pacient);
        Pages.dashboardPage().openNewCallDash(pacient);
        Pages.fullCardPage().verifyNewCall(pacient);//почему-то 2 педиатрический сразу. С Таким адресом два участка
        $(By.xpath("//*[contains(text(),'#2 Педиатрический')]")).shouldNotBe(Condition.visible);
        $(By.xpath("//*[contains(text(),'#6 Педиатрический')]")).shouldNotBe(Condition.visible);
    }

    @Test(groups = "CD", description = "вызов из Колл-Центра по api, ребенок по МКАБ без КЛАДР.")
    @Epic("Создание вызова")
    @RetryCountIfFailed(2)
    public void testCallCenterChildMkab2() throws IOException, InterruptedException, JSONException {
        Pacient pacient = new Pacient("Profile20");
        enterSite.enterCalldoctorFromMis();
        Pages.createCallPage().createCall_Api(pacient);
        Pages.dashboardPage().openNewCallDash(pacient);
        Pages.fullCardPage().verifyNewCall(pacient);
        $(By.xpath("//*[contains(text(),'#6 Педиатрический')]")).shouldBe(Condition.visible);
        $(By.xpath("//*[contains(text(),'#2 Педиатрический')]")).shouldNotBe(Condition.visible);
    }
}

// TODO: 18.08.2018 сделать пару тестов для проверки кладра (выписать адреса с которыми было много проблем)
// TODO: 29.08.2018 делать проверку на время создания вызова
// TODO: 29.08.2018 сделать тест добавление адреса в адресное пространство