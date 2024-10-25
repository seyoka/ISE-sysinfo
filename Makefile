.PHONY: all clean
.PHONY: libsysinfo/libsysinfo.so

MODULES=sysInfo cpuInfo pciInfo usbInfo
HEADERS=$(MODULES:%=project/%.h)

LIBSYSINFO_PATH=project/libsysinfo
LIBSYSINFOSO_PATH=$(LIBSYSINFO_PATH)/libsysinfo.so
MYPROJECT=project/Main

JFREECHART_JAR = lib/jfreechart-1.0.13.jar
JCOMMON_JAR = lib/jcommon-1.0.23.jar

all: $(HEADERS) $(MYPROJECT).class $(LIBSYSINFOSO_PATH)

$(MYPROJECT).class: $(MYPROJECT).java $(LIBSYSINFOSO_PATH)
	javac -cp project:$(LIBSYSINFO_PATH):$(JFREECHART_JAR):$(JCOMMON_JAR) $< -d project

$(HEADERS): %.h: %.java
	javac -h $<

$(LIBSYSINFOSO_PATH):
	$(MAKE) -C LIBSYSINFO_PATH

# Clean up compiled files
clean:
	rm -rf project/*.class $(LIBSYSINFOSO_PATH)
