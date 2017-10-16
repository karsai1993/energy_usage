package laszlo.karsai.diploma.work;

public class OperatingSystemMXBeanHandler {
	double totalPhysicalMemorySize;
	double freePhysicalMemorySize;
	double usedPhysicalMemorySize;
	double systemCpuLoad;
	
	
	
	public OperatingSystemMXBeanHandler(double totalPhysicalMemorySize, double freePhysicalMemorySize,
			double systemCpuLoad) {
		super();
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
		this.freePhysicalMemorySize = freePhysicalMemorySize;
		this.systemCpuLoad = systemCpuLoad;
	}
	public double getTotalPhysicalMemorySize() {
		return totalPhysicalMemorySize;
	}
	public void setTotalPhysicalMemorySize(double totalPhysicalMemorySize) {
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
	}
	public double getFreePhysicalMemorySize() {
		return freePhysicalMemorySize;
	}
	public void setFreePhysicalMemorySize(double freePhysicalMemorySize) {
		this.freePhysicalMemorySize = freePhysicalMemorySize;
	}
	public double getUsedPhysicalMemorySize() {
		return this.totalPhysicalMemorySize - this.freePhysicalMemorySize;
	}
	public void setUsedPhysicalMemorySize(double usedPhysicalMemorySize) {
		this.usedPhysicalMemorySize = usedPhysicalMemorySize;
	}
	public double getSystemCpuLoad() {
		return systemCpuLoad;
	}
	public void setSystemCpuLoad(double systemCpuLoad) {
		this.systemCpuLoad = systemCpuLoad;
	}
	
}
