
# Makefile by -b.t. (thomas@adc.gsfc.nasa.gov) from 
# modified version first created by Bob Schaefer

.KEEP_STATE:

# If you're using JDK 1.1 you'll need to specify the path to
# your SWING library directory.  Not necessary with JDK 1.2,
# which includes that version of SWING.
#SWING =		/usr/local/java/swing-1.1/
#JAVA =	$(JAVA_HOME)/bin/java


# JCFLAGS == javac flags
JCFLAGS = -g # -deprecation
# CLASSPATH
CPATH = 	.:/usr/local/jdk1.2.2/lib/classes.zip:/usr/local/jaxp-1.1ea2/jaxp.jar:/usr/local/jaxp-1.1ea2/xalan.jar:/usr/local/jaxp-1.1ea2/crimson.jar 
#CPATH = 	.:/usr/local/jdk1.2.2/lib/classes.zip

# archive w/jar flags
ARCHIVE_BUILD_ARGS=cvf
ARCHIVE_NAME=xdf.jar

# checkin/checkout are flags for RCS
MAJOR_VERSION=0.0
CHECKIN_FLAGS = -m'Major Version:$(MAJOR_VERSION)' -r$(MAJOR_VERSION)
CHECKOUT_FLAGS = -r$(MAJOR_VERSION) -l

# sources
XDF_SOURCE_PATH = gov/nasa/gsfc/adc/xdf
XDF_BIN_PATH = ../
XDF_SOURCES = \
	$(XDF_SOURCE_PATH)/Array.java \
	$(XDF_SOURCE_PATH)/Axis.java \
	$(XDF_SOURCE_PATH)/AxisLocationOutOfBoundsException.java \
	$(XDF_SOURCE_PATH)/BaseObject.java \
	$(XDF_SOURCE_PATH)/BinaryFloatDataFormat.java \
	$(XDF_SOURCE_PATH)/BinaryIntegerDataFormat.java \
	$(XDF_SOURCE_PATH)/Constants.java \
	$(XDF_SOURCE_PATH)/DataFormat.java \
	$(XDF_SOURCE_PATH)/DataCube.java \
	$(XDF_SOURCE_PATH)/DelimitedXMLDataIOStyle.java \
	$(XDF_SOURCE_PATH)/Field.java \
	$(XDF_SOURCE_PATH)/FieldAxis.java \
	$(XDF_SOURCE_PATH)/FloatDataFormat.java \
	$(XDF_SOURCE_PATH)/Group.java \
	$(XDF_SOURCE_PATH)/IntegerDataFormat.java \
	$(XDF_SOURCE_PATH)/Locator.java \
	$(XDF_SOURCE_PATH)/Log.java \
	$(XDF_SOURCE_PATH)/NoDataException.java \
	$(XDF_SOURCE_PATH)/Note.java \
	$(XDF_SOURCE_PATH)/Parameter.java \
	$(XDF_SOURCE_PATH)/ParameterGroup.java \
	$(XDF_SOURCE_PATH)/Reader.java \
	$(XDF_SOURCE_PATH)/SaxDocumentHandler.java \
	$(XDF_SOURCE_PATH)/SetDataException.java \
	$(XDF_SOURCE_PATH)/StringDataFormat.java \
	$(XDF_SOURCE_PATH)/Structure.java \
	$(XDF_SOURCE_PATH)/TaggedXMLDataIOStyle.java \
	$(XDF_SOURCE_PATH)/Unit.java \
 	$(XDF_SOURCE_PATH)/UnitDirection.java \
	$(XDF_SOURCE_PATH)/Units.java \
	$(XDF_SOURCE_PATH)/Utility.java \
	$(XDF_SOURCE_PATH)/Value.java \
	$(XDF_SOURCE_PATH)/ValueGroup.java \
	$(XDF_SOURCE_PATH)/XMLDataIOStyle.java

#	$(XDF_SOURCE_PATH)/ExponentialDataFormat.java \
#	$(XDF_SOURCE_PATH)/ErroredValue.java \
#	$(XDF_SOURCE_PATH)/FieldGroup.java \
#	$(XDF_SOURCE_PATH)/FieldRelation.java \
#	$(XDF_SOURCE_PATH)/FormattedIOCmd.java \
#	$(XDF_SOURCE_PATH)/FormattedXMLDataIOStyle.java \
#	$(XDF_SOURCE_PATH)/ReadCellFormattedIOCmd.java \
#	$(XDF_SOURCE_PATH)/RepeatFormattedIOCmd.java \
#	$(XDF_SOURCE_PATH)/SkipCharFormattedIOCmd.java \


XDF_CLASSES = $(XDF_SOURCES:%.java=%.class)

ALL_SOURCES = $(XDF_SOURCES)
ALL_CLASSES = $(ALL_SOURCES:%.java=%.class)

# Pattern rules:
%.class : %.java
	javac $(JCFLAGS) -classpath $(CPATH) $<
#	@echo Compiled:$< : $@ : $* : $+ : $^ 
#	@echo moving $(basename $@) to $(patsubst %,$(XDF_BIN_PATH)/%,$(dir $@)) 
#	@mv $@ $(patsubst %,$(XDF_BIN_PATH)/%,$(dir $@))


#
# RULES. 
#

all: compile archive 

archive:
	jar $(ARCHIVE_BUILD_ARGS) $(ARCHIVE_NAME) ./gov 

compile: $(ALL_CLASSES)
	@echo mv $(XDF_SOURCE_PATH)/*.class $(XDF_BIN_PATH)/$(XDF_SOURCE_PATH)
	@mv $(XDF_SOURCE_PATH)/*.class $(XDF_BIN_PATH)/$(XDF_SOURCE_PATH)

# checkin changes 
checkin:
	# ci $(CHECKIN_FLAGS) $(ALL_SOURCES)
	@echo checkin doesnt do anything anymore

# check out the latest version
checkout:
	# co $(CHECKOUT_FLAGS) $(ALL_SOURCES)
	@echo checkout doesnt do anything anymore

# build the XDF package
xdf:        $(XDF_CLASSES)

#
# scrub-a-dub-dub
#
clean:
	-rm -f $(ALL_CLASSES)

distclean: xdfdistclean
	#-rcsclean -u
	@echo distclean doesnt do anything anymore

xdfclean:
	-rm -f $(XDF_CLASSES)

xdfdistclean: xdfclean
	# -rcsclean -u $(XDF_SOURCE_PATH)/*
	@echo xdfdistclean doesnt do anything anymore


