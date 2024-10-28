.PHONY: all clean
.PHONY: libsysinfo/libsysinfo.so

MODULES=sysInfo cpuInfo pciInfo usbInfo
HEADERS=$(MODULES:%=project/%.h)

LIBSYSINFO_PATH=project/libsysinfo
LIBSYSINFOSO_PATH=project/libsysinfo/libsysinfo.so
MYPROJECT=project/DashboardApp

JFREECHART_JAR = lib/jfreechart-1.0.13.jar
JCOMMON_JAR = lib/jcommon-1.0.23.jar

all: $(HEADERS) $(MYPROJECT).class $(LIBSYSINFOSO_PATH)

$(MYPROJECT).class: $(MYPROJECT).java $(LIBSYSINFOSO_PATH)
	javac -cp project:project/libsysinfo:$(JFREECHART_JAR):$(JCOMMON_JAR) $< -d project

$(HEADERS): %.h: %.java
	javac -h project $<

$(LIBSYSINFOSO_PATH):
	$(MAKE) -C project/libsysinfo
