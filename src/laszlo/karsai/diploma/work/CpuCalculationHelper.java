package laszlo.karsai.diploma.work;

public class CpuCalculationHelper {
	int processId;
	double elapsedTime;
	
	public CpuCalculationHelper (int processId, double elapsedTime) {
		this.processId = processId;
		this.elapsedTime = elapsedTime;
	}
	
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public double getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	
}
