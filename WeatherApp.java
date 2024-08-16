import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherApp extends JFrame implements ActionListener {
    private JTextField cityField;
    private JButton searchButton;
    private JTextArea resultArea;

    private static final String API_KEY = "your_api_key"; // Replace with your OpenWeatherMap API key

    public WeatherApp() {
        setTitle("Weather App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter City:"));
        cityField = new JTextField(15);
        inputPanel.add(cityField);
        searchButton = new JButton("Search");
        inputPanel.add(searchButton);
        searchButton.addActionListener(this);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String city = cityField.getText().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a city name.");
            }
        }
    }

    private void fetchWeatherData(String city) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            parseAndDisplayWeatherData(content.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching weather data. Please check the city name and try again.");
        }
    }

    private void parseAndDisplayWeatherData(String jsonResponse) {
        try {
            JSONObject jsonObj = new JSONObject(jsonResponse);

            // Extract data
            String cityName = jsonObj.getString("name");
            JSONObject main = jsonObj.getJSONObject("main");
            double temp = main.getDouble("temp");
            int humidity = main.getInt("humidity");
            JSONObject weatherObj = jsonObj.getJSONArray("weather").getJSONObject(0);
            String weatherDescription = weatherObj.getString("description");

            // Display data
            resultArea.setText("City: " + cityName + "\n");
            resultArea.append("Temperature: " + temp + " °C\n");
            resultArea.append("Humidity: " + humidity + " %\n");
            resultArea.append("Weather: " + weatherDescription + "\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error parsing weather data.");
        }
    }

    public static void main(String[] args) {
        new WeatherApp();
    }
}

