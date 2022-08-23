package com.ramon.app.models;

import com.ramon.app.junitCurso.exceptions.DineroInsuficienteException;
import com.ramon.app.junitCurso.models.Banco;
import com.ramon.app.junitCurso.models.Cuenta;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;

    //Se ejecuta antes de ejecutar cada metodo de prueba
    @BeforeEach
    void initMetodoTest() {
        cuenta = new Cuenta("Ramon", new BigDecimal("40000"));
    }

    //Se ejecuta despues de ejecutar cada metodo de prueba
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando metodo de prueba");
    }

    //Se ejecuta una vez y es antes que se empiecen a ejecutar las pruebas
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando todas las pruebas");
    }

    //Se ejecuta una vez y es despues de que se ejecuten todas las pruebas
    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando todas las pruebas");
    }

    @Tag("cuenta")
    @Nested
    class PruebasDeCuentaYSaldo {
        @Test
        @DisplayName("Probando el nombre de la cuenta corriente")//Cambia el nombre del metodo a nivel de pruebas
        void testNombreCuenta() {
            cuenta = new Cuenta("Ramon", new BigDecimal("40000.50"));
            //cuenta.setPersona("Ramon");
            String esperado = "Ramon";
            String real = cuenta.getPersona();
            assertEquals(esperado, real, "El nombre de la cuenta no es el que se esperaba");
            assertEquals(true, real.equals("Ramon"), "El nombre de la cuenta debe ser igual al real");
        }

        @Test
        void testSaldoCuenta() {
            cuenta = new Cuenta("Ramon", new BigDecimal("40000.50"));
            assertEquals(40000.50, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        }

        @Test
        @DisplayName("Probando el testInfo y el testReporter")
        void testReferenciaCuenta(TestInfo testInfo, TestReporter testReporter) {
            //Para obtener
            System.out.println("Ejecutando ... " + testInfo.getDisplayName());
            System.out.println("Ejecutando ... " + testInfo.getTestMethod());
            System.out.println("Ejecutando ... " + testInfo.getTestClass());
            System.out.println("Ejecutando ... " + testInfo.getTags());

            //Para loguear mensajes
            testReporter.publishEntry("Ramon esto es un testReporter");

            Cuenta cuentaUno = new Cuenta("Jonh", new BigDecimal("8900.99"));
            Cuenta cuentaDos = new Cuenta("Jonh", new BigDecimal("8900.99"));

//        assertNotEquals(cuentaUno, cuentaDos);
            assertEquals(cuentaUno, cuentaDos);
        }
    }

    //@RepeatedTest repite un numero de veces el test
    @DisplayName("Probando Debito Cuenta Repetir")
    @RepeatedTest(value = 5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuenta(RepetitionInfo info) throws DineroInsuficienteException {

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Repeticion numero tres");
        }
        cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
        cuenta.debito(new BigDecimal(1000));
        assertNotNull(cuenta.getSaldo());
        assertEquals(9000, cuenta.getSaldo().intValue());
        assertNotEquals("10000", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")//Se puede etiquetar a nivel de clase o metodo por metodo, esto para
    //indicar que tanto queremos ejecutar indicando el tag
    @Nested
    @DisplayName("Probando las pruebas parametrizadas")
    class PruebasParametrizadas {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @ValueSource(strings = {"100", "200", "500", "10000"})
        void testDebitoCuenta(String monto) throws DineroInsuficienteException {
            cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvSource({"1, 100", "2, 200", "3, 500", "4, 1000"})
        void testDebitoCuentaCSVSource(String index, String monto) throws DineroInsuficienteException {
            cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
            System.out.println(index + " --> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvSource({"200, 100", "400, 200", "700, 500", "1000, 999"})
        void testDebitoCuentaCSVSource2(String saldo, String monto) throws DineroInsuficienteException {
            System.out.println(saldo + " --> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCSVFile(String monto) throws DineroInsuficienteException {
            cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
            System.out.println(" --> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data2.csv")//Se debe de generar en /resources el archivo data2.csv cada linea
        //es una entrada y el numero de parametros se separa con comas
        void testDebitoCuentaCSVFile2(String saldo, String monto) throws DineroInsuficienteException {
            System.out.println(saldo + " --> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @MethodSource("montoList")
        void testDebitoCuentaMethodSource(String monto) throws DineroInsuficienteException {
            cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
            System.out.println(" --> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    //Metodo llamado como entrada para pruebas con anotacion @MethodSource
    static List<String> montoList() {
        return Arrays.asList("100", "200", "500", "1000", "2000");
    }

    @Test
    void testCreditoCuenta() {
        cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
        cuenta.credito(new BigDecimal(1000));
        assertNotNull(cuenta.getSaldo());
        assertEquals(11000, cuenta.getSaldo().intValue());
        assertNotEquals("10000", cuenta.getSaldo().toPlainString());
    }

    //Revisa si el metodo lanza la exception indicada
    @Test
    void testDineroInsuficienteExceptionCuenta() {
        cuenta = new Cuenta("Ramon", new BigDecimal("10000"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(11000));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(actual, esperado);

    }

    @Test
    void testTrasnsferirDineroCuentas() throws DineroInsuficienteException {
        Cuenta cuentaOrigen = new Cuenta("Juan", new BigDecimal("2500"));
        Cuenta cuentaDetino = new Cuenta("Luis", new BigDecimal("1500"));

        Banco banco = new Banco();
        banco.setNombre("Santander");

        banco.transferir(cuentaOrigen, cuentaDetino, new BigDecimal(500));
        assertEquals("2000", cuentaOrigen.getSaldo().toPlainString());
        assertEquals("2000", cuentaDetino.getSaldo().toPlainString());

    }

    @Test
    @Disabled
//Deshabilita la prueba
    void testRelacionBancoCuentas() throws DineroInsuficienteException {
        fail();
        Cuenta cuentaOrigen = new Cuenta("Juan", new BigDecimal("2500"));
        Cuenta cuentaDetino = new Cuenta("Luis", new BigDecimal("1500"));

        Banco banco = new Banco();
        banco.addCuenta(cuentaOrigen);
        banco.addCuenta(cuentaDetino);
        banco.setNombre("Santander");

        banco.transferir(cuentaOrigen, cuentaDetino, new BigDecimal(500));

        //Ejecuta todos los asserts, aunque alguno falle, ejecuta todos
        assertAll(
                () -> {
                    assertEquals("200", cuentaOrigen.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals("2000", cuentaDetino.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals(1, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Santander", cuentaOrigen.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Juan", banco.getCuentas().stream()
                            .filter(cuenta -> cuenta.getPersona().equals("Juan"))
                            .findFirst()
                            .get().getPersona());
                }
        );
    }

    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + " : " + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "18.0.1.1")
    void testJavaVersion() {

    }

    @Test
        //@DisplayName("Probando el sueldo de la cuenta")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        Assumptions.assumeTrue(esDev);//Si se cumple la condicion se ejecutan los siguientes assertions
        assertNotNull(cuenta.getSaldo());
    }

    @Tag("SO")
    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
            System.out.println("Test en Windows");
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMAC() {
            System.out.println("Test en Linux-Mac");
        }
    }

    @Tag("jre")
    @Nested
    class JRETest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJava8() {
            System.out.println("Solo java 8");
        }

        @Test
        @DisabledOnJre(JRE.JAVA_18)
        void testDisabledJDK18() {
        }
    }

    @Tag("enviroment")
    @Nested
    class VariablesDeEntornoTest {
        @Test
        void testLeerVariablesDeEntorno() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-18.0.1.1.*")
        void testJavaHome() {
        }


        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
        void testNumeroProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "prod")
        void testVariablesDeEntornoPersonalizada() {
        }
    }

    @Tag("timeout")
    @Nested
    class TimeOutTest{
        //Por defecto es en segundos
        //La prueba debe de durar como maximo 5 segundos
        @Test
        @Timeout(5)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(5);
        }

        //La prueba debe de durar 5000 milisegundos como maximo
        @Test
        @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(5);
        }

        //La prueba debe de durar como maximo 5 segundos
        @Test
        void timeOutAssertion() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.SECONDS.sleep(6);
            });
        }
    }
}