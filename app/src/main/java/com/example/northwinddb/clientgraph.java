package com.example.northwinddb;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS;
import static com.github.mikephil.charting.utils.ColorTemplate.VORDIPLOM_COLORS;

public class clientgraph extends AppCompatActivity {

    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@info706.cwwvo42siq12.ap-southeast-2.rds.amazonaws.com:1521:ORCL";
    private static final String DEFAULT_USERNAME = "19477870";
    private static final String DEFAULT_PASSWORD = "Grimorio12!";

    private Connection connection;
    private static final String TAG = "MyDBTag";

    private String year_slicer = "2015";
    private Spinner spinner;

    float productData[] = {10f, 20f, 40f, 50f, 10f};
    String productNames[] = { "Ernst Handel", "QUICK-Stop", "Bon app", "Vaffeljernet", "Que Delícia"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientgraph);

        setUpChart();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_slicer = spinner.getSelectedItem().toString();
                setupBarChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                year_slicer = spinner.getSelectedItem().toString();
            }
        });

    }


    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public void setUpChart(){
        Log.i(TAG, "Running Query");
        List<String> list = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        spinner = (Spinner) findViewById(R.id.myspinner);

        try {
            String sql = "select distinct extract(year from order_date) syear from orders order by 1";
            this.connection = createConnection();
            Statement stmt = connection.createStatement();
            Log.i(TAG, sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                list.add(rs.getString(1));
            }
            connection.close();

            ArrayAdapter<String> dAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            dAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dAdapt);

        }
        catch (Exception e){
            Log.i(TAG, "ERROR !!!  " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.i(TAG, "ERROR Detail !!!  " + sw.toString());

        }




    }

    private void setupBarChart(){
        BarChart bChart = (BarChart) findViewById(R.id.bar_chart);
        List<BarEntry> barEntries = new ArrayList<>();

        bChart.setDrawBarShadow(false);
        bChart.setDrawValueAboveBar(true);
        bChart.setMaxVisibleValueCount(50);
        bChart.setPinchZoom(false);
        bChart.setDrawGridBackground(true);
        bChart.getDescription().setEnabled(false);


        try {
            String sql = "select * from V_SALESBYCUSTYEAR WHERE sYEAR=" + year_slicer + " AND rownum < 6 order by ordervalue desc  ";
            this.connection = createConnection();
            Statement stmt = connection.createStatement();
            Log.i(TAG, sql);
            ResultSet rs = stmt.executeQuery(sql);

            int i=0;

            Log.i(TAG, "Ejecuto SQL");

            while (rs.next()){
                Log.i(TAG, "Name:" + rs.getString(3) + "/ Dat:" + rs.getString(1));
                productNames[i]= rs.getString(3);
                productData[i]= rs.getFloat(1);
       //         barEntries.add(new BarEntry(i, rs.getFloat(1)));
                i++;
            }
            connection.close();



        }
        catch (Exception e){
            Log.i(TAG, "ERROR !!!  " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.i(TAG, "ERROR Detail !!!  " + sw.toString());

        }


        for (int i = 0; i < productData.length; i++) {
            barEntries.add(new BarEntry(i, productData[i]));
        }

        BarDataSet bds = new BarDataSet(barEntries, "Clients");
        bds.setColors(COLORFUL_COLORS);

        BarData data = new BarData(bds);

        bChart.setData(data);

        final ArrayList<String> xAxisLabels = new ArrayList<>();
        for (int i = 0; i < productNames.length; i++) {
            Log.i(TAG, "Name2:" + productNames[i] + " I:" + i);
                    xAxisLabels.add(productNames[i]);
        }
        XAxis xAxis = bChart.getXAxis();

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabels.get((int) value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);


        data.setBarWidth(0.9f);
        bChart.animateY(500);
        bChart.invalidate();

    }

}
