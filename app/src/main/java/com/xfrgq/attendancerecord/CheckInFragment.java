package com.xfrgq.attendancerecord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.xfrgq.attendancerecord.HttpRequestHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckInFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin, container, false);

        TextView locationText = view.findViewById(R.id.location_text);
        TextView timeText = view.findViewById(R.id.time_text);
        LinearLayout checkInList = view.findViewById(R.id.checkin_list);

        // 获取传递过来的用户名并显示
        String username = requireActivity().getIntent().getStringExtra("username");
        if (username != null) {
        }

        // Mock location
        String mockLocation = "Latitude: 37.7749, Longitude: -122.4194";
        locationText.setText(mockLocation);

        // Update current time
        updateCurrentTime(timeText);

        // Fetch and populate check-in items
        fetchCheckInItems(checkInList, locationText.getText().toString(), timeText, username);

        return view;
    }

    private void updateCurrentTime(TextView timeText) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        timeText.setText(currentTime);
    }

    private void fetchCheckInItems(LinearLayout checkInList, String location, TextView timeText, String username) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                HttpURLConnection connection = HttpRequestHelper.createPostRequest("/getCheckInItems", json);
                String response = HttpRequestHelper.getResponse(connection);

                JSONObject jsonResponse = new JSONObject(response);
                if ("success".equals(jsonResponse.optString("status"))) {
                    JSONArray items = jsonResponse.optJSONArray("items");

                    if (items != null) {
                        getActivity().runOnUiThread(() -> populateCheckInList(checkInList, items, location, timeText, username));
                    }
                } else {
                    showToast("Failed to fetch check-in items.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Network error while fetching check-in items.");
            }
        }).start();
    }

    private void populateCheckInList(LinearLayout checkInList, JSONArray items, String location, TextView timeText, String username) {
        checkInList.removeAllViews();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);

            if (item != null) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.checkin_item, checkInList, false);

                TextView itemName = itemView.findViewById(R.id.item_name);
                TextView itemDescription = itemView.findViewById(R.id.item_description);
                TextView itemDeadline = itemView.findViewById(R.id.item_deadline);
                Button checkInButton = itemView.findViewById(R.id.item_checkin_button);

                String name = item.optString("name", "Unknown");
                String description = item.optString("description", "No description available");
                String deadline = item.optString("deadline", "No deadline");

                itemName.setText(name);
                itemDescription.setText(description);
                itemDeadline.setText("Deadline: " + deadline);

                checkInButton.setOnClickListener(v -> {
                    updateCurrentTime(timeText); // Update the time before sending
                    String currentTime = timeText.getText().toString();

                    new Thread(() -> {
                        try {
                            // Prepare JSON request body
                            JSONObject jsonRequest = new JSONObject();
                            jsonRequest.put("item_name", name);
                            jsonRequest.put("location", location);
                            jsonRequest.put("time", currentTime);
                            jsonRequest.put("username", username);

                            // Send POST request
                            HttpURLConnection connection = HttpRequestHelper.createPostRequest("/checkIn", jsonRequest);
                            String response = HttpRequestHelper.getResponse(connection);

                            // Parse the response
                            JSONObject jsonResponse = new JSONObject(response);
                            if ("success".equals(jsonResponse.optString("status"))) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Checked in to: " + name, Toast.LENGTH_SHORT).show();

                                    // Refresh the check-in items list
                                    fetchCheckInItems(checkInList, location, timeText, username);
                                });
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Network error while checking in.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                });

                checkInList.addView(itemView);
            }
        }
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
    }
}