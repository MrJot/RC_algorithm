package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.paint.Color.*;

public class Controller implements Initializable{

    private Color B = Color.web("#292929");
    private Color R = Color.web("#f24343");

    private ArrayList<Double> input = new ArrayList<>();
    private ArrayList<Double> output = new ArrayList<>();
    private String path;

    private boolean cV = true;

    @FXML
    private TextArea textArea;

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private TextField saveField;

    @FXML
    private Button justaButton;

    @FXML
    private Button plotButton;

    @FXML
    private Button calcButton;

    @FXML
    private Button saveButton;

    @FXML
    private LineChart chart;

    @FXML
    private Label byV;

    @FXML
    private void read(ActionEvent event) {
        BufferedReader br;

        saveButton.setText("Save");
        saveButton.setTextFill(B);
        saveButton.setStyle(".button");
        saveButton.setDisable(false);

        try {
            input.clear();
            output.clear();
            label.setTextFill(BLACK);
            label.setText("File name");
            path = new String(textField.getText());
            br = new BufferedReader(new FileReader(path));
            try {
                input.clear();
                String x;
                textArea.setText("Data from your file!");
                while ((x = br.readLine()) != null) {
                    textArea.setText(textArea.getText() + "\n" + x);
                }
                br.close();
                Pattern pat= Pattern.compile("([0-9]+)((\\.|,)?[0-9]+)?");
                Matcher matcher=pat.matcher(textArea.getText());
                while (matcher.find()) {
                    input.add(Double.parseDouble(matcher.group().replace(",", ".")));
                }
                plotButton.setVisible(true);
                plotButton.setDisable(false);
                saveButton.setVisible(false);
                saveField.setVisible(false);
                justaButton.setStyle(".button");
                justaButton.setTextFill(B);
                justaButton.setText("Read");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            label.setTextFill(R);
            label.setText("File not found!");

            System.out.println(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void plot(ActionEvent event) {
        try {
            chart.getData().clear();
            chart.setTitle("Rainflow-counting algorithm");
            XYChart.Series series = new XYChart.Series();
            series.setName(path);
            for (int i = 0; i < input.size(); ++i)
                series.getData().add(new XYChart.Data(i, input.get(i)));
            chart.getData().add(series);
            chart.setVisible(true);
            calcButton.setVisible(true);
            plotButton.setStyle(".button");
            plotButton.setTextFill(B);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void calc(ActionEvent event) {
        try {

            ArrayList<String> index = new ArrayList<>();

            boolean max = true;
            for (int i = 0; i < input.size(); i = i + 2) {
                if (input.get(i) < input.get(i + 1)) {
                    break;
                } else if (input.get(i) > input.get(i + 1)) {
                    max = false;
                    break;
                }
            }

            boolean twoness = true;
            if (input.size()%2 != 0) {
                twoness = false;
            }
            else ;


            if (max) {
                for (int i = 0; i < input.size(); ++i)
                    output.add(input.get(i));

                ArrayList<Double> data = new ArrayList<>();
                for (int i = 0; i < input.size(); ++i)
                    data.add(input.get(i));

                /**
                 * Variables
                 */
                double bottom, top;
                ArrayList<Integer> counter = new ArrayList<>();
                if (twoness) {
                    for (int i = 0; i < data.size() / 2; ++i)
                        counter.add(i);
                }
                else {
                    for (int i = 0; i < (data.size() + 1) / 2; ++i)
                        counter.add(i);
                }

                for (int i = 0; i < 2; ++i)
                    data.add(data.get(data.size()-1));

                int maxSize =  counter.get(counter.size()-1);
                for (int i = 0; i < maxSize; i = counter.get(0)) {
                    /**
                     * My code here!!!
                     */
                    bottom = data.get(2*i);
                    top = data.get(2*i+1);
                    int where = 2*i+1;

                    if (bottom > data.get(2*(i+1))) {
                        counter.remove(new Integer(i));
                        continue;
                    }


                    if (bottom <= data.get(2*(i+1))) {
                        counter.remove(new Integer(i));
                        i++;
                        do {
                            if (top < data.get(2 * i + 1)) {
                                top = data.get(2 * i + 1);
                                data.set(2 * i + 1, data.get(where));
                                data.set(where, top);
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top == data.get(2 * i + 1)) {
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top > data.get(2 * i + 1)) {
                                ++i;
                                continue;
                            }
                        } while (bottom <= data.get(2*i) && 2*i < 2*maxSize);
                    }
                }

                textArea.setText("Table of sigmas:\n");

                for (int i = 0; i < output.size(); i=i+2) {
                    output.set(i, data.get(i+1) - data.get(i));
                }

                /**
                 *
                 * HERE NEXT SIDE OF DATA
                 *
                 */
                data.clear();
                for (int i = 1; i < input.size(); ++i)
                    data.add(input.get(i)*(-1));

                twoness = !twoness;
                /**
                 * Variables
                 */
                counter.clear();
                if (twoness) {
                    for (int i = 0; i < data.size() / 2; ++i)
                        counter.add(i);
                }
                else {
                    for (int i = 0; i < (data.size() + 1) / 2; ++i)
                        counter.add(i);
                }

                for (int i = 0; i < 2; ++i)
                    data.add(data.get(data.size()-1));

                maxSize =  counter.get(counter.size()-1);
                for (int i = 0; i < maxSize-1; i = counter.get(0)) {
                    /**
                     * My code here!!!
                     */
                    bottom = data.get(2 * i);
                    top = data.get(2 * i + 1);
                    int where = 2 * i + 1;

                    if (bottom > data.get(2 * (i + 1))) {
                        counter.remove(new Integer(i));
                        continue;
                    }

                    if (bottom <= data.get(2 * (i + 1))) {
                        counter.remove(new Integer(i));
                        i++;
                        do {
                            if (top < data.get(2 * i + 1)) {
                                top = data.get(2 * i + 1);
                                data.set(2 * i + 1, data.get(where));
                                data.set(where, top);
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top == data.get(2 * i + 1)) {
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top > data.get(2 * i + 1)) {
                                ++i;
                                continue;
                            }
                        } while (bottom <= data.get(2 * i) && 2 * i < 2 * maxSize);
                    }
                }

                for (int i = 0; i < output.size()-1; i=i+2) {
                    output.set(i+1, data.get(i+1) - data.get(i));
                }
            }

            if (!max) {
                for (int i = 0; i < input.size(); ++i)
                    output.add(input.get(i));

                ArrayList<Double> data = new ArrayList<>();
                for (int i = 0; i < input.size(); ++i)
                    data.add((-1)*input.get(i));

                /**
                 * Variables
                 */
                double bottom, top;
                ArrayList<Integer> counter = new ArrayList<>();
                if (twoness) {
                    for (int i = 0; i < data.size() / 2; ++i)
                        counter.add(i);
                }
                else {
                    for (int i = 0; i < (data.size() + 1) / 2; ++i)
                        counter.add(i);
                }

                for (int i = 0; i < 2; ++i)
                    data.add(data.get(data.size()-1));

                int maxSize =  counter.get(counter.size()-1);
                for (int i = 0; i < maxSize-1; i = counter.get(0)) {
                    /**
                     * My code here!!!
                     */
                    bottom = data.get(2*i);
                    top = data.get(2*i+1);
                    int where = 2*i+1;

                    if (bottom > data.get(2*(i+1))) {
                        counter.remove(new Integer(i));
                        continue;
                    }


                    if (bottom <= data.get(2*(i+1))) {
                        counter.remove(new Integer(i));
                        i++;
                        do {
                            if (top < data.get(2 * i + 1)) {
                                top = data.get(2 * i + 1);
                                data.set(2 * i + 1, data.get(where));
                                data.set(where, top);
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top == data.get(2 * i + 1)) {
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top > data.get(2 * i + 1)) {
                                ++i;
                                continue;
                            }
                        } while (bottom <= data.get(2*i) && 2*i < 2*maxSize);
                    }
                }

                textArea.setText("Table of sigmas:");

                for (int i = 0; i < output.size(); i=i+2) {
                    output.set(i, data.get(i+1) - data.get(i));
                }

                /**
                 *
                 * HERE NEXT SIDE OF DATA
                 *
                 */
                data.clear();
                for (int i = 1; i < input.size(); ++i)
                    data.add(input.get(i));

                twoness = !twoness;
                /**
                 * Variables
                 */
                counter.clear();
                if (twoness) {
                    for (int i = 0; i < data.size() / 2; ++i)
                        counter.add(i);
                }
                else {
                    for (int i = 0; i < (data.size() + 1) / 2; ++i)
                        counter.add(i);
                }

                for (int i = 0; i < 2; ++i)
                    data.add(data.get(data.size()-1));

                maxSize =  counter.get(counter.size()-1);
                for (int i = 0; i < maxSize; i = counter.get(0)) {
                    /**
                     * My code here!!!
                     */
                    bottom = data.get(2 * i);
                    top = data.get(2 * i + 1);
                    int where = 2 * i + 1;

                    if (bottom > data.get(2 * (i + 1))) {
                        counter.remove(new Integer(i));
                        continue;
                    }

                    if (bottom <= data.get(2 * (i + 1))) {
                        counter.remove(new Integer(i));
                        i++;
                        do {
                            if (top < data.get(2 * i + 1)) {
                                top = data.get(2 * i + 1);
                                data.set(2 * i + 1, data.get(where));
                                data.set(where, top);
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top == data.get(2 * i + 1)) {
                                counter.remove(new Integer(i));
                                ++i;
                                continue;
                            }
                            if (top > data.get(2 * i + 1)) {
                                ++i;
                                continue;
                            }
                        } while (bottom <= data.get(2 * i) && 2 * i < 2 * maxSize);
                    }
                }
                for (int i = 0; i < output.size()-1; i=i+2) {
                    output.set(i+1, data.get(i+1) - data.get(i));
                }
            }

            for (int i = 0; i < output.size(); ++i) {
                textArea.setText(textArea.getText() + "\n" + "From " + i + ": " + output.get(i));
            }

            calcButton.setStyle(".button");
            calcButton.setTextFill(B);
            plotButton.setVisible(false);
            plotButton.setDisable(true);
            saveButton.setVisible(true);
            saveField.setVisible(true);
            calcButton.setVisible(false);
            justaButton.setStyle("-fx-background-color: #0093ff");
            justaButton.setText("New!");
            justaButton.setTextFill(WHITE);
            textField.setText(path);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void save(ActionEvent event) {

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(saveField.getText()), "utf-8"));
            for (int i = 0; i < output.size(); ++i)
                writer.write(Double.toString(output.get(i))+"\n");

            saveButton.setText("Done!");
            saveButton.setDisable(true);
            saveButton.setTextFill(B);
            saveButton.setStyle(".button");
            saveField.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }

    @FXML
    private void change(MouseEvent arg0) {
        if (cV) {
            byV.setText("Vadym.A.Denysenko@gmail.com");
            byV.setLayoutX(500);
            cV = false;
        }
        else {
            byV.setText("by V.");
            byV.setLayoutX(575);
            cV = true;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        justaButton.setStyle("-fx-background-color: #0093ff");
        justaButton.setTextFill(WHITE);
        textField.setStyle("-fx-prompt-text-fill: #0093ff");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            textField.setText("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\data.txt");
            saveField.setText("C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\results.txt");
        }
        else {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                textField.setText("/Users/" + System.getProperty("user.name") + "/Desktop/data.txt");
                saveField.setText("/Users/" + System.getProperty("user.name") + "/Desktop/results.txt");
            }
            else {
                textField.setText("/home/" + System.getProperty("user.name") + "/Desktop/data.txt");
                saveField.setText("/home/" + System.getProperty("user.name") + "/Desktop/results.txt");
            }
        }
        plotButton.setStyle("-fx-background-color: #0093ff");
        plotButton.setTextFill(WHITE);
        calcButton.setStyle("-fx-background-color: #0093ff");
        calcButton.setTextFill(WHITE);
        saveField.setStyle("-fx-prompt-text-fill: #0093ff");
        saveButton.setStyle("-fx-background-color: #0093ff");
        saveButton.setTextFill(WHITE);
    }
}
