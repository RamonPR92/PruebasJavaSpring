package com.ramon.app.mockito.services;

import com.ramon.app.mockito.models.Examen;
import com.ramon.app.mockito.repositories.DummyExamenRepository;
import com.ramon.app.mockito.repositories.DummyPreguntaRepository;
import com.ramon.app.mockito.repositories.ExamenRepository;
import com.ramon.app.mockito.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Habilita las anotaciones de Mockito (forma 2)
@ExtendWith(MockitoExtension.class)
//Baja el modo estricto en mockito
@MockitoSettings(strictness = Strictness.LENIENT)
class EscuelaExamenServiceTest {
    //NOTA
    //No se pueden hacer simulaciones de metodos estaticos, privados o final

    //Interfaces
    @Mock
    DummyExamenRepository examenRepository;
    @Mock
    DummyPreguntaRepository preguntaRepository;

    //Inyecta los repositorios en el servicio, el servicio debera ser la implementacion no la interfaz
    @InjectMocks
    EscuelaExamenService examenService;

    List<Examen> examenes;
    List<String> preguntas;

    //El argumentCaptor puede ser inyectado (forma 2)
    @Captor
    ArgumentCaptor<Long> captorLong;

    @BeforeEach
    void setUp() {
        //Habilita el uso de anotaciones de mockito (forma 1)
        //MockitoAnnotations.openMocks(this);
        //------------------------------------
        //Una forma de intyectar los objetos
//        examenRepository = Mockito.mock(ExamenRepository.class);
//        preguntaRepository = Mockito.mock(PreguntaRepository.class);
//        examenService = new EscuelaExamenService(examenRepository, preguntaRepository);

        examenes = Arrays.asList(
                new Examen(null, "Matematicas"),
                new Examen(6L, "Programacion"),
                new Examen(-7L, "Historia"),
                new Examen(8L, "Redes"));

        preguntas =Arrays.asList(
                "aritmetica",
                "integrales",
                "trigonometria"
        );
    }

    @Test
    void findExamenPorNombre() {
        //Si se llama al metodo findAll se retornaran los datos que indiquemos
        when(examenRepository.findAll()).thenReturn(examenes);

        Optional<Examen> examen = examenService.findExamenPorNombre("Historia");
        assertTrue(examen.isPresent());
        assertEquals(7L, examen.orElseThrow().getId());
        assertEquals("Historia", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        //Los datos a probar los generamos por nuestra cuenta
        List<Examen> datos = Collections.emptyList();

        //Si se llama al metodo findAll se retornaran los datos que indiquemos
        when(examenRepository.findAll()).thenReturn(datos);

        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");
        assertFalse(examen.isPresent());
        assertThrows(NoSuchElementException.class, () -> examen.orElseThrow().getId());
        assertThrows(NoSuchElementException.class,() -> examen.orElseThrow().getNombre());
    }

    @Test
    void testPreguntaExamen() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        Examen examen  = examenService.findExamenPorNombreConPreguntas("Programacion");
        assertEquals(3, examen.getPreguntas().size());
    }

    @Test
    void testPreguntaExamenVerify() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        Examen examen  = examenService.findExamenPorNombreConPreguntas("Programacion");
        assertEquals(3, examen.getPreguntas().size());
        //Verifica que los metodos hubieran sido invocados durante la prueba,
        // tambien toma en cuenta los parametros usados
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testPreguntaExamenCuandoNoEncuentraLaListaVerify() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        Examen examen  = examenService.findExamenPorNombreConPreguntas("Ciencias");
        assertNotNull(examen);
        verify(examenRepository).findAll();
        //Falla por que nunca es invocado ya que no encuentra el examen Ciencias en la lista
        //Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }

    @Test
    void testGuardarExamen() {
        examenes.get(3).setPreguntas(preguntas);
        when(examenRepository.save(any(Examen.class))).thenReturn(examenes.get(3));
        Examen examen = examenService.guardarExamen(examenes.get(3));
        assertNotNull(examen);
        assertEquals(8L, examen.getId());
        assertEquals("Redes", examen.getNombre());

        verify(preguntaRepository).savePreguntas(anyList());
        verify(examenRepository).save(any(Examen.class));
    }

    @Test
    void testGuardarExamenConGeneradorId() {
        //Given, preparamos nuestro entorno de pruebas
        when(examenRepository.save(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(++secuencia);
                return examen;
            }
        });

        //When, Cuando se ejecuta el ambiente de pruebas
        examenes.get(0).setPreguntas(preguntas);
        Examen examen = examenService.guardarExamen(examenes.get(0));

        //Then, Se verifican los resultados
        assertNotNull(examen);
        assertEquals(9L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());

        verify(preguntaRepository).savePreguntas(anyList());
        verify(examenRepository).save(any(Examen.class));
    }

    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(examenes);
        //Cuando se ejecute findPreguntasPorExamenId con null como parametro
        //devolvera la excepcion NullPointerException
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(NullPointerException.class);

        //Probamos findExamenPorNombreConPreguntas pasando matematicas cuyo id es null
        //y en algun momento llama a findPreguntasPorExamenId
        assertThrows(NullPointerException.class, () ->{
            examenService.findExamenPorNombreConPreguntas("Matematicas");
        });

        verify(examenRepository).findAll();
        //Verificamos que se halla llamado a findPreguntasPorExamenId con null como parametro
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPorNombreConPreguntas("Redes");

        verify(examenRepository).findAll();
//        verify(preguntaRepository).findPreguntasPorExamenId(argThat( arg -> arg == null));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat((args) -> args.equals(8L)));
    }

    @Test
    void testArgumentMatchersDos() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPorNombreConPreguntas("Historia");

        verify(examenRepository).findAll();
//        verify(preguntaRepository).findPreguntasPorExamenId(argThat( arg -> arg == null));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgumentMatchers()));
    }

    //El argumentmMatcher puede ser mas complejo,
    public class MiArgumentMatchers implements ArgumentMatcher<Long>{

        private Long argument;
        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "Es para un mensaje personalizado de error que imprime mockito en caso de que falle el test "
                    + argument + " debe ser un entero mayor a 0";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(examenRepository.findAll()).thenReturn(examenes);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        examenService.findExamenPorNombreConPreguntas("Redes");

        verify(examenRepository).findAll();
        //Captura el valor que se pasa al metodo para despues usarlo en los asserts
        //Forma 1
        //ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenId(captorLong.capture());

        assertEquals( 8L, captorLong.getValue());
    }

    @Test
    void testDoThrow() {
        //Indica que cuando se invoque el metodo se lanzara una excepcion
        doThrow(IllegalArgumentException.class).when(preguntaRepository).savePreguntas(anyList());
        assertThrows(IllegalArgumentException.class, () ->{
            examenes.get(3).setPreguntas(preguntas);
            examenService.guardarExamen(examenes.get(3));
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findAll()).thenReturn(examenes);
        //Al formular el doAnswer le indicamos ciertas validaciones a los parametros de entrada
        //y al final indicamos el metodo al que se le aplicara
        doAnswer(invocation ->{
            Long id = invocation.getArgument(0);
            return id >= 5L ? preguntas : null;
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Redes");
        assertTrue(examen.getPreguntas().size() > 0);

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(8L);
    }

    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findAll()).thenReturn(examenes);
        //Manda a llamar a la implementacion real, sale un error si se intenta simular la interfaz
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPreguntas("Redes");

        assertEquals(8L, examen.getId());
        assertEquals("Redes", examen.getNombre());
    }

    @Test
    void testSpy() {
        //Las pruebas que usan Spy deben de evitarse lo mas posible pero cuando no haya de otra
        //se utilizaran para probar directamente con los metodos reales
        ExamenRepository examenRepository = spy(DummyExamenRepository.class);
        PreguntaRepository preguntaRepository = spy(DummyPreguntaRepository.class);
        ExamenService examenService = new EscuelaExamenService(examenRepository, preguntaRepository);

        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
    }

    @Test
    void testOrdenDeInvocacion() {
        when(examenRepository.findAll()).thenReturn(examenes);

        examenService.findExamenPorNombreConPreguntas("Programacion");//6L
        examenService.findExamenPorNombreConPreguntas("Redes");//8L

        InOrder inOrder = inOrder(preguntaRepository);

        //verifica el orden de las llamadas, si no estan en orden la prueba falla
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(8L);
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(examenes);
        examenService.findExamenPorNombreConPreguntas("Redes");

        //Verifica las veces que fue llamado el metodo
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(8L);
        //Verifica que se ejecute el metodo por lo menos un numero de veces
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(8L);
        //Verifica que se ejecute el metodo por lo menos una vez
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(8L);
        //Verifica que se ejecute el metodo a lo mucho un numero de veces dado
        verify(preguntaRepository, atMost(1)).findPreguntasPorExamenId(8L);
        //Verifica que el metodo sea ejecutado a lo mucho una vez
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(8L);
    }
}