package test.unit;
import org.junit.Assert;
import org.junit.Test;
import model.MeterReading;

public class MeterReadingTest {
	
	@Test
	public void testCalcDeviationStandard1(){//current value > e-invoice value
		MeterReading mr = new MeterReading();
		mr.setCurrentValue(1318);
		mr.setEInvoiceValue(1315.0);
		Assert.assertEquals(3, mr.calcDeviation(), 0.0001);
	}
	
	@Test
	public void testCalcDeviationStandard2(){//current value < e-invoice value
		MeterReading mr = new MeterReading();
		mr.setCurrentValue(1090);
		mr.setEInvoiceValue(1105.0);
		Assert.assertEquals(-15, mr.calcDeviation(), 0.0001);
	}
	
	@Test
	public void testCalcDeviationException(){//there is no e-invoice
		MeterReading mr = new MeterReading();
		mr.setCurrentValue(92);
		Assert.assertEquals(0, mr.calcDeviation(), 0.0001);
	}
	
	@Test
	public void testGetConsumption(){
		MeterReading mr = new MeterReading();
		mr.setPreviousValue(1240);
		mr.setCurrentValue(1318);
		Assert.assertEquals(78, mr.getConsumption(), 0.0001);
	}
}
