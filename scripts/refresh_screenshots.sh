#!/bin/bash
# refresh_screenshots.sh — Automatically capture and update all catalog snapshots persistently.
# Usage: ./scripts/refresh_screenshots.sh [Optional Single Snippet Title]

set -e

TARGET_SNIPPET="$1"
OUTPUT_DIR="./docs/images"
mkdir -p "$OUTPUT_DIR"

# 1. Enable SystemUI Demo Mode for pixel-perfect and consistent top status bar clock/battery/network
./scripts/configure_screen.sh

get_time_ms() {
  adb shell date +%s%3N
}

# Map KTX Snippet Titles to output filenames
declare -A FILENAMES
FILENAMES["Map Initialization"]="map_init_golden.png"
FILENAMES["Animate Camera (Coroutines)"]="camera_animation_golden.gif"
FILENAMES["Camera Idle Events Flow"]="camera_animation_golden.png" # Shares close-up camera state
FILENAMES["Fine Location Flow"]="location_flow_fine_london.png"
FILENAMES["Coarse Location Flow"]="location_flow_coarse_paris.png"
FILENAMES["Marker Cluster Click Flow"]="clustering_clicks.gif"
FILENAMES["Marker Collection Click Flow"]="collection_clicks.gif"

capture_snippet() {
  local title="$1"
  local filename="${FILENAMES[$title]}"
  
  if [ -z "$filename" ]; then
    filename=$(echo "$title" | tr '[:upper:]' '[:lower:]' | tr -cd 'a-z0-9 ' | tr ' ' '_').png
  fi
  
  local IS_ANIMATION=false
  if [[ "$filename" == *.gif ]]; then
    IS_ANIMATION=true
  fi
  
  echo "------------------------------------------------"
  echo "Capturing: '$title' -> '$filename'..."
  echo "------------------------------------------------"
  
  # Force-stop the demo application to ensure fresh states
  adb shell am force-stop com.google.maps.android.ktx.demo
  sleep 1
  
  # Pre-configure mock location providers BEFORE launching the app to prevent Flow subscription cancellation
  if [ "$title" == "Fine Location Flow" ]; then
    echo "Pre-configuring GPS mock provider..."
    adb shell appops set com.android.shell android:mock_location allow
    adb shell appops set com.google.maps.android.ktx.demo android:mock_location allow
    adb shell cmd location providers add-test-provider gps
    adb shell cmd location providers set-test-provider-enabled gps true
  elif [ "$title" == "Coarse Location Flow" ]; then
    echo "Pre-configuring Network mock provider..."
    adb shell appops set com.android.shell android:mock_location allow
    adb shell appops set com.google.maps.android.ktx.demo android:mock_location allow
    adb shell cmd location providers add-test-provider network
    adb shell cmd location providers set-test-provider-enabled network true
  fi
  
  # Launch the demo dynamically inside the targeted snippet view
  adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"$title\""
  sleep 4 # Let base maps load and vectors render
  
  if [ "$IS_ANIMATION" == "true" ]; then
    echo "Starting background screen recording (6s limit)..."
    adb shell screenrecord --time-limit 6 /sdcard/temp_anim.mp4 &
    RECORD_PID=$!
    sleep 1.5
  fi
  
  # Route custom automated actions depending on the snippet category
  case "$title" in
    "Animate Camera (Coroutines)")
      echo "Simulating Click on Animate Camera Button..."
      # Tap Animate Camera floating button near bottom center
      adb shell input tap 540 2220
      sleep 4
      ;;
    "Fine Location Flow")
      echo "Injecting sequential GPS trajectory coordinates (London)..."
      
      # Point 1: London Center
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location gps --location 51.5074,-0.1278 --time $TIME_MS
      sleep 2.5
      # Point 2: London South
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location gps --location 51.4800,-0.1200 --time $TIME_MS
      sleep 2.5
      # Point 3: London East
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location gps --location 51.4950,-0.0800 --time $TIME_MS
      sleep 3.5
      ;;
    "Coarse Location Flow")
      echo "Injecting sequential Network trajectory coordinates (Paris)..."
      
      # Point 1: Paris Center
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location network --location 48.8566,2.3522 --time $TIME_MS
      sleep 5.5
      # Point 2: Paris South
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location network --location 48.8300,2.3400 --time $TIME_MS
      sleep 5.5
      # Point 3: Paris East
      TIME_MS=$(get_time_ms)
      adb shell cmd location providers set-test-provider-location network --location 48.8400,2.3800 --time $TIME_MS
      sleep 6.5
      ;;
    "Marker Cluster Click Flow")
      echo "Simulating Cluster Badge Tapping and individual item clicks..."
      # Tap Cluster Badge to zoom in
      adb shell input tap 540 1200
      sleep 2.5
      # Sweep tap in center to hit one of the expanded markers
      for offset in 0 -60 60; do
        for yoffset in 0 -60 60; do
          adb shell input tap $((540 + offset)) $((1200 + yoffset))
          sleep 0.4
        done
      done
      sleep 1.5
      ;;
    "Marker Collection Click Flow")
      echo "Simulating Custom Marker Tapping and Clickable Circle Tapping..."
      # Click custom Azure Marker
      adb shell input tap 540 1650
      sleep 2.5
      # Click Clickable Circle (robust sweep around its expected screen coordinate)
      for offset in 0 -50 50; do
        for yoffset in 0 -50 50; do
          adb shell input tap $((600 + offset)) $((1300 + yoffset))
          sleep 0.4
        done
      done
      sleep 1.5
      ;;
  esac

  if [ "$IS_ANIMATION" == "true" ]; then
    echo "Waiting for screen recording to complete..."
    wait $RECORD_PID || true
    
    # Pull the MP4 video
    adb pull /sdcard/temp_anim.mp4 "$OUTPUT_DIR/temp_anim.mp4"
    adb shell rm /sdcard/temp_anim.mp4
    
    # Convert MP4 to GIF using ffmpeg
    if command -v ffmpeg >/dev/null 2>&1; then
      ffmpeg -y -i "$OUTPUT_DIR/temp_anim.mp4" -vf "fps=15,scale=360:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" "$OUTPUT_DIR/$filename"
      echo "Successfully generated animation $filename"
    else
      echo "Error: ffmpeg is required to convert MP4 to GIF!"
      mv "$OUTPUT_DIR/temp_anim.mp4" "$OUTPUT_DIR/${filename%.gif}.mp4"
      echo "Saved raw MP4 to $OUTPUT_DIR/${filename%.gif}.mp4 instead."
    fi
    
    # Clean up temp MP4
    rm -f "$OUTPUT_DIR/temp_anim.mp4"
  else
    # Capture screen view
    adb shell screencap -p /sdcard/temp_ref.png
    
    # Pull screenshot to local docs folder
    adb pull /sdcard/temp_ref.png "$OUTPUT_DIR/$filename"
    adb shell rm /sdcard/temp_ref.png
    
    # If ImageMagick is installed locally, resize to 360px widths for beautiful markdown rendering efficiency
    if command -v convert >/dev/null 2>&1; then
      convert "$OUTPUT_DIR/$filename" -resize 360x "$OUTPUT_DIR/$filename"
      echo "Resized $filename to elegant 360px width."
    else
      echo "Notice: install ImageMagick (convert) to automatically scale screenshots to compact sizes."
    fi
  fi

  # Clean up mock locations if they were enabled
  if [ "$title" == "Fine Location Flow" ]; then
    adb shell cmd location providers set-test-provider-enabled gps false
    adb shell cmd location providers remove-test-provider gps
  elif [ "$title" == "Coarse Location Flow" ]; then
    adb shell cmd location providers set-test-provider-enabled network false
    adb shell cmd location providers remove-test-provider network
  fi
}

if [ -n "$TARGET_SNIPPET" ]; then
  # Capture a single targeted snippet catalog item
  capture_snippet "$TARGET_SNIPPET"
else
  # Capture all snippets in order
  capture_snippet "Map Initialization"
  capture_snippet "Animate Camera (Coroutines)"
  capture_snippet "Fine Location Flow"
  capture_snippet "Coarse Location Flow"
  capture_snippet "Marker Cluster Click Flow"
  capture_snippet "Marker Collection Click Flow"
fi

# 2. Turn off SystemUI Demo Mode to restore actual hardware bars
./scripts/configure_screen.sh off

echo "KTX Catalog snapshot refresh complete! References saved to $OUTPUT_DIR/"
