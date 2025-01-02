package com.xfrgq.attendancerecord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xfrgq.attendancerecord.HttpRequestHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        TextView presentCountText = view.findViewById(R.id.present_count);
        TextView absentCountText = view.findViewById(R.id.absent_count);

        RecyclerView recyclerView = view.findViewById(R.id.attendance_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 获取用户名
        String username = requireActivity().getIntent().getStringExtra("username");

        // 获取统计数据和考勤记录
        fetchAttendanceStatistics(username, presentCountText, absentCountText);
        fetchAttendanceRecords(username, recyclerView);

        return view;
    }

    private void fetchAttendanceStatistics(String username, TextView presentCountText, TextView absentCountText) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);

                HttpURLConnection connection = HttpRequestHelper.createPostRequest("/getAttendanceStatistics", json);
                String response = HttpRequestHelper.getResponse(connection);

                JSONObject jsonResponse = new JSONObject(response);
                getActivity().runOnUiThread(() -> {
                    if ("success".equals(jsonResponse.optString("status"))) {
                        int presentCount = jsonResponse.optInt("present_count");
                        int absentCount = jsonResponse.optInt("absent_count");

                        presentCountText.setText(String.valueOf(presentCount));
                        absentCountText.setText(String.valueOf(absentCount));
                    } else {
                        Toast.makeText(getContext(), "无法获取统计数据", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void fetchAttendanceRecords(String username, RecyclerView recyclerView) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);

                HttpURLConnection connection = HttpRequestHelper.createPostRequest("/getAttendanceRecords", json);
                String response = HttpRequestHelper.getResponse(connection);

                JSONObject jsonResponse = new JSONObject(response);
                getActivity().runOnUiThread(() -> {
                    if ("success".equals(jsonResponse.optString("status"))) {
                        JSONArray recordsArray = jsonResponse.optJSONArray("records");
                        List<AttendanceRecord> records = new ArrayList<>();

                        for (int i = 0; i < recordsArray.length(); i++) {
                            JSONObject recordJson = null;
                            try {
                                recordJson = recordsArray.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            records.add(new AttendanceRecord(
                                    recordJson.optString("time"),
                                    recordJson.optString("location"),
                                    recordJson.optString("name"),
                                    recordJson.optString("description"),
                                    recordJson.optString("created_at"),
                                    recordJson.optString("deadline")
                            ));
                        }

                        AttendanceAdapter adapter = new AttendanceAdapter(records);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "无法获取考勤记录", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

class AttendanceRecord {
    String time;
    String location;
    String name;
    String description;
    String createdAt;
    String deadline;

    AttendanceRecord(String time, String location, String name, String description, String createdAt, String deadline) {
        this.time = time;
        this.location = location;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }
}

class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private final List<AttendanceRecord> records;

    AttendanceAdapter(List<AttendanceRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_record, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);
        holder.timeText.setText(record.time);
        holder.locationText.setText(record.location);
        holder.nameText.setText(record.name);
        holder.descriptionText.setText(record.description);
        holder.createdAtText.setText(record.createdAt);
        holder.deadlineText.setText(record.deadline);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView locationText;
        TextView nameText;
        TextView descriptionText;
        TextView createdAtText;
        TextView deadlineText;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.attendance_time);
            locationText = itemView.findViewById(R.id.attendance_location);
            nameText = itemView.findViewById(R.id.attendance_name);
            descriptionText = itemView.findViewById(R.id.attendance_description);
            createdAtText = itemView.findViewById(R.id.attendance_created_at);
            deadlineText = itemView.findViewById(R.id.attendance_deadline);
        }
    }
}
