package laszlo.karsai.diploma.work;

public class ProcessData {
	String name;
	double memUsage;
	double cpuUsage;
	String windowTitle;
	int index;
	int processId;
	double cpuTimeInSec;
	String status;
	String userName;
	
	public ProcessData (int processId, String name, double memUsage, double cpuUsage) {
		this.processId = processId;
		this.name = name;
		this.memUsage = memUsage;
		this.cpuUsage = cpuUsage;
	}

	/*public ProcessData(String name, double memUsage, double cpuUsage,
			String windowTitle, int index, int processId,
			double cpuTimeInSec, String status, String userName) {
		//super();
		this.name = name;
		this.memUsage = memUsage;
		this.cpuUsage = cpuUsage;
		this.windowTitle = windowTitle;
		this.index = index;
		this.processId = processId;
		this.cpuTimeInSec = cpuTimeInSec;
		this.status = status;
		this.userName = userName;
	}*/
	
	public ProcessData(String name, double memUsage, double cpuUsage,
			String windowTitle, int processId,
			double cpuTimeInSec, String status, String userName) {
		this.name = name;
		this.memUsage = memUsage;
		this.cpuUsage = cpuUsage;
		this.windowTitle = windowTitle;
		this.processId = processId;
		this.cpuTimeInSec = cpuTimeInSec;
		this.status = status;
		this.userName = userName;
	}
	
	public ProcessData(String name, int index, double memUsage, double cpuUsage,
			String windowTitle, int processId) {
		this.name = name;
		this.index = index;
		this.memUsage = memUsage;
		this.cpuUsage = cpuUsage;
		this.windowTitle = windowTitle;
		this.processId = processId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getCpuTimeInSec() {
		return cpuTimeInSec;
	}

	public void setCpuTimeInSec(double cpuTimeInSec) {
		this.cpuTimeInSec = cpuTimeInSec;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMemUsage() {
		return memUsage;
	}
	public void setMemUsage(double memUsage) {
		this.memUsage = memUsage;
	}
	public double getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
}
