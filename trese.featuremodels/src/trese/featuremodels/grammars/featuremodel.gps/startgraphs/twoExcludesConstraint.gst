<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="twoExcludesConstraint" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n19879"/>
        <node id="n19880"/>
        <node id="n19881"/>
        <node id="n19882"/>
        <node id="n19883"/>
        <node id="n19884"/>
        <node id="n19885"/>
        <node id="n19886"/>
        <node id="n19887"/>
        <node id="n19888"/>
        <node id="n19889"/>
        <node id="n19890"/>
        <node id="n19891"/>
        <node id="n19892"/>
        <node id="n19893"/>
        <edge from="n19889" to="n19882">
            <attr name="label">
                <string>mandatory</string>
            </attr>
        </edge>
        <edge from="n19888" to="n19893">
            <attr name="label">
                <string>or</string>
            </attr>
        </edge>
        <edge from="n19888" to="n19890">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19881" to="n19884">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19888" to="n19879">
            <attr name="label">
                <string>or</string>
            </attr>
        </edge>
        <edge from="n19885" to="n19891">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19892" to="n19883">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19888" to="n19892">
            <attr name="label">
                <string>or</string>
            </attr>
        </edge>
        <edge from="n19886" to="n19886">
            <attr name="label">
                <string>string:"F"</string>
            </attr>
        </edge>
        <edge from="n19891" to="n19891">
            <attr name="label">
                <string>string:"B"</string>
            </attr>
        </edge>
        <edge from="n19882" to="n19881">
            <attr name="label">
                <string>alt</string>
            </attr>
        </edge>
        <edge from="n19893" to="n19887">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19882" to="n19885">
            <attr name="label">
                <string>alt</string>
            </attr>
        </edge>
        <edge from="n19885" to="n19892">
            <attr name="label">
                <string>excludes</string>
            </attr>
        </edge>
        <edge from="n19889" to="n19888">
            <attr name="label">
                <string>optional</string>
            </attr>
        </edge>
        <edge from="n19889" to="n19889">
            <attr name="label">
                <string>BaseLine</string>
            </attr>
        </edge>
        <edge from="n19882" to="n19880">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19890" to="n19890">
            <attr name="label">
                <string>string:"D"</string>
            </attr>
        </edge>
        <edge from="n19884" to="n19884">
            <attr name="label">
                <string>string:"C"</string>
            </attr>
        </edge>
        <edge from="n19892" to="n19893">
            <attr name="label">
                <string>excludes</string>
            </attr>
        </edge>
        <edge from="n19883" to="n19883">
            <attr name="label">
                <string>string:"E"</string>
            </attr>
        </edge>
        <edge from="n19880" to="n19880">
            <attr name="label">
                <string>string:"A"</string>
            </attr>
        </edge>
        <edge from="n19879" to="n19886">
            <attr name="label">
                <string>name</string>
            </attr>
        </edge>
        <edge from="n19887" to="n19887">
            <attr name="label">
                <string>string:"G"</string>
            </attr>
        </edge>
    </graph>
</gxl>
