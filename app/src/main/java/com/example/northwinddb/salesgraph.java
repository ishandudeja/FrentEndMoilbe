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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class salesgraph extends AppCompatActivity {

    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@info706.cwwvo42siq12.ap-southeast-2.rds.amazonaws.com:1521:ORCL";
    private static final String DEFAULT_USERNAME = "19477870";
    private static final String DEFAULT_PASSWORD = "Grimorio12!";

    private Connection connection;
    private static final String TAG = "MyDBTag";

    private String year_slicer = "2015";
    private Spinner spinner;

    float productData[] = {10f, 20f, 40f, 50f};
    String productNames[] = { "Shoes", "Pencil", "AB", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesgraph);

        setUpChart();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_slicer = spinner.getSelectedItem().toString();
                setupLineChart();
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

    private void setupLineChart(){
        LineChart lChart = (LineChart) findViewById(R.id.line_chart);
        lChart.getDescription().setEnabled(true);

        Description d = new Description();
        d.setText("GRAPH OF SALES PER MONTH");
        d.setTextSize(15);
        lChart.setDescription(d);

        List<Entry> barEntries = new ArrayList<>();

        try {
            String sql = "SELECT * FROM V_SALESBYYEAR2M WHERE sYEAR='" + year_slicer + "' ORDER BY SMONTH";
            this.connection = createConnection();
            Statement stmt = connection.createStatement();
            Log.i(TAG, sql);
            ResultSet rs = stmt.executeQuery(sql);

            int i=0;
            while (rs.next()){
                i++;
                barEntries.add(new BarEntry(rs.getInt(3), rs.getInt(1)));
            }
            connection.close();
        }
        catch (Exception e){
            Log.i(TAG, "ERROR !!!  " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.i(TAG, "ERROR Detail !!!  " + sw.toString());

        }


        LineDataSet lds = new LineDataSet(barEntries, "US Dollars");
        lds.setHighlightEnabled(true);
        lds.setLineWidth(5);
        lds.setCircleColor(Color.GREEN); // data point color
        lds.setCircleRadius(5); // data point size
        lds.setHighLightColor(Color.GREEN);
        lds.setValueTextSize(14);
        lds.setColors(Color.GREEN);
        lds.setDrawFilled(true);
        lds.setFillColor(Color.GREEN);
        LineData data = new LineData(lds);
        lChart.setData(data);

        lChart.animateY(500);
        lChart.invalidate();  //redrawh
    }

}
