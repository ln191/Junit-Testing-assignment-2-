package net.sf.javaanpr.test;

import net.sf.javaanpr.imageanalysis.CarSnapshot;
import net.sf.javaanpr.intelligence.Intelligence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Created by lucas on 04-03-2017.
 */
@RunWith(Parameterized.class)
public class RecognitionAllTest {
    private String expectedPlate;
    private File currentPlate;
    private Intelligence intel;

    @Before
    public void initialize() throws IOException, SAXException, ParserConfigurationException {
         intel = new Intelligence();
    }

    public RecognitionAllTest(File currentPlate, String expectedPlate ){
        this.currentPlate = currentPlate;
        this.expectedPlate = expectedPlate;

    }
    @Parameters
    public static Collection<Object[]> testDataCreator() {
        String snapshotDirPath = "src/test/resources/snapshots";
        String resultsPath = "src/test/resources/results.properties";
        Properties properties = new Properties();
        try {
            InputStream resultsStream = new FileInputStream(new File(resultsPath));
            properties.load(resultsStream);
            resultsStream.close();
        }
        catch (IndexOutOfBoundsException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File snapshotDir = new File(snapshotDirPath);
        File[] snapshots = snapshotDir.listFiles();

        Collection<Object[]> dataForOneImage= new ArrayList();
        for (File file : snapshots)
        {
            String name = file.getName();
            String plateExpected = properties.getProperty(name);
            dataForOneImage.add(new Object[]{file, plateExpected });
        }   return dataForOneImage;
    }

    @Test
    public void testAllSnapshots() throws Exception {
        CarSnapshot carSnap = new CarSnapshot(new FileInputStream(currentPlate));

        assertNotNull("carSnap is null", carSnap);
        assertNotNull("carSnap.image is null", carSnap.getImage());
        assertNotNull(expectedPlate);

        String numberPlate = intel.recognize(carSnap, false);

//        assertEquals(expectedPlate,numberPlate);
        assertThat(numberPlate, equalTo(expectedPlate));
        carSnap.close();
    }

}
