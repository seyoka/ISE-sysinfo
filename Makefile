.PHONY: libsysinfo/libsysinfo.so

MODULES=sysInfo cpuInfo pciInfo usbInfo
HEADERS=$(MODULES:%=project/%.h)

LIBSYSINFO_PATH=project/libsysinfo
LIBSYSINFOSO_PATH=$(LIBSYSINFO_PATH)/libsysinfo.so
MYPROJECT=project/Main

all: $(HEADERS) $(MYPROJECT).class $(LIBSYSINFOSO_PATH)

$(MYPROJECT).class: $(MYPROJECT).java $(LIBSYSINFOSO_PATH)
	javac -cp project:$(LIBSYSINFO_PATH) $< -d project

$(HEADERS): %.h: %.java
	javac -h $<

$(LIBSYSINFOSO_PATH):
	$(MAKE) -C LIBSYSINFO_PATH

