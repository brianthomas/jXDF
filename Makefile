
# Makefile by -b.t. (thomas@adc.gsfc.nasa.gov) from 
# modified version first created by Bob Schaefer

# CVS $Id$

.KEEP_STATE:

# If you're using JDK 1.1 you'll need to specify the path to
# your SWING library directory.  Not necessary with JDK 1.2,
# which includes that version of SWING.
#SWING =		/usr/local/java/swing-1.1/
#JAVA =	$(JAVA_HOME)/bin/java


# JCFLAGS == javac flags
JCFLAGS = -g 
# CLASSPATH
CPATH = 	.:/usr/local/jdk1.2.2/lib/classes.zip:/usr/local/xml-tr2/xml.jar
#CPATH = 	.:/usr/local/jdk1.2.2/lib/classes.zip

# archive w/jar flags
ARCHIVE_BUILD_ARGS=cvf
ARCHIVE_NAME=xdf.jar

# checkin/checkout are flags for RCS
MAJOR_VERSION=0.0
CHECKIN_FLAGS="-m'Major Version:$(MAJOR_VERSION)' -r$(MAJOR_VERSION)" 
CHECKOUT_FLAGS="-r$(MAJOR_VERSION) -l" 

# documentation
#  NOTE: DONT make DOC_LOCATION "." you can wind up deleting
#  the source code (see below)
ADC_XML_HOMEPAGE = http://tarantella.gsfc.nasa.gov/xml
DOC_LOCATION = ./doc 
DOC_BUILD_ARGS = -use -author -version -windowtitle "eXtensible Data Format API" \
                 -doctitle "<H2>DOC TITLE</H2>" \
                 -header "<h2><a href="$(ADC_XML_HOMEPAGE)">ADC XML Group<a></h2><A HREF="http://www.nasa.gov/"><IMG SRC="http://adc.gsfc.nasa.gov/adc/images/nasalogo.gif" BORDER=0 ALT=\"NASA insignia\"></A>" \
                 -footer "" \
                 -bottom "<h2><A HREF="mailto:xdf@xml.gsfc.nasa.gov">Contact the ADC XDF Group</a></h2>"



# sources
XDF_SRC_DIR = ./src

TEST_SOURCES=TestXDF.java
TEST_LOAD_FILE=XDF_delimit_array.xml XDF_mtable_array.xml
TEST_CLASSES = $(VIEW_SOURCES:%.java=%.class)

ALL_SOURCES = $(TEST_SOURCES)
ALL_CLASSES = $(ALL_SOURCES:%.java=%.class)

# Pattern rules:
%.class : %.java
	javac $(JCFLAGS) -classpath $(CPATH) $<

#
# RULES. 
#

all: compile archive doc

compile: 
	@echo "compiling sources in $(XDF_SRC_DIR)..."; 
	( cd $(XDF_SRC_DIR); $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' all; ) 
#	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' 'EXTRA_CFLAGS=$(XX_EXTRA_CFLAGS)' 'EXTRA_INCLUDES=$(EXTRA_INCLUDES)' 'EXTRA_LIBS=$(EXTRA_LIBS)' 'CC=$(CC)' all; ) \

archive:
	jar $(ARCHIVE_BUILD_ARGS) $(ARCHIVE_NAME) ./gov 

doc: xdfdoc 

# checkin changes 
checkin:
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' checkin; ) 

# check out the latest version
checkout:
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' checkout; ) 

# build the XDF package
xdf: 
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' xdf; ) 

xdfdoc:
	javadoc -d $(DOC_LOCATION) $(DOC_BUILD_ARGS) $(XDF_SRC_DIR)/gov/nasa/gsfc/adc/xdf/*java

# run the test program for the XDF package
test: compile
	@echo java -classpath .:$(CPATH) TestXDF $(TEST_LOAD_FILE) 
	java -classpath .:$(CPATH) TestXDF $(TEST_LOAD_FILE) 

#
# scrub-a-dub-dub
#
clean: 
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' clean; ) 
	-rm -f ./gov/nasa/gsfc/adc/xdf/*.class
	#-rm -f $(ALL_CLASSES)

distclean: xdfdistclean
	#-rcsclean -u
	# Bad! if doc dir == source dir you delete it all!! 
	-rm -r $(DOC_LOCATION)/* 

xdfclean:
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' xdfclean; ) 

xdfdistclean: xdfclean
	(cd $(XDF_SRC_DIR);  $(MAKE) $(MFLAGS) 'MFLAGS=$(MFLAGS)' xdfdistclean; )


