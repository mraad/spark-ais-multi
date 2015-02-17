import pyhs2


class Toolbox(object):
    def __init__(self):
        self.label = "Toolbox"
        self.alias = "Spark SQL Toolbox"
        self.tools = [Tool]


class Tool(object):
    def __init__(self):
        self.label = "Query AIS Table"
        self.description = "Query AIS Table"
        self.canRunInBackground = False

    def getParameterInfo(self):
        paramHost = arcpy.Parameter(
            name="in_host",
            displayName="Host",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramHost.value = "boot2docker"

        paramPort = arcpy.Parameter(
            name="in_port",
            displayName="Port",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramPort.value = "49710"

        paramWhere = arcpy.Parameter(
            name="in_where",
            displayName="Where",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramWhere.value = "year=2009 and month=01 and day=20 and hour between 7 and 9"

        paramFC = arcpy.Parameter(
            name="outputFC",
            displayName="outputFC",
            direction="Output",
            datatype="DEFeatureClass",
            parameterType="Derived")
        return [paramWhere, paramHost, paramPort, paramFC]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        outputName = "AIS"
        outputFC = "in_memory/" + outputName
        if arcpy.Exists(outputFC):
            arcpy.management.Delete(outputFC)
        spref = arcpy.SpatialReference(4326)
        arcpy.management.CreateFeatureclass("in_memory", outputName, "POINT", spatial_reference=spref)
        arcpy.management.AddField(outputFC, "MMSI", "TEXT")
        arcpy.management.AddField(outputFC, "HEADING", "LONG")
        arcpy.management.AddField(outputFC, "ZULU", "TEXT")
        arcpy.management.AddField(outputFC, "YEAR", "LONG")
        arcpy.management.AddField(outputFC, "MONTH", "LONG")
        arcpy.management.AddField(outputFC, "DAY", "LONG")
        arcpy.management.AddField(outputFC, "HOUR", "LONG")
        with arcpy.da.InsertCursor(outputFC,
                                   ['SHAPE@XY', 'MMSI', 'HEADING', 'ZULU', 'YEAR', 'MONTH', 'DAY', 'HOUR']) as cursor:
            with pyhs2.connect(host=parameters[1].value,
                               port=int(parameters[2].value),
                               timeout=60000,
                               authMechanism="NOSASL",
                               user='root',
                               password='root',
                               database='default') as conn:
                with conn.cursor() as cur:
                    cur.execute(
                        "select lon,lat,mmsi,heading,zulu,year,month,day,hour from ais where " + parameters[0].value)
                    for row in cur.fetch():
                        x = float(row[0])
                        y = float(row[1])
                        mmsi = row[2]
                        head = int(row[3])
                        zz = row[4]
                        yy = int(row[5])
                        mm = int(row[6])
                        dd = int(row[7])
                        hh = int(row[8])
                        cursor.insertRow(((x, y), mmsi, head, zz, yy, mm, dd, hh))
                    cur.close()
                conn.close()
            del cursor
        parameters[3].value = outputFC
        return
