# Specify the parent image from which we build
FROM stereolabs/zed:3.7-gl-devel-cuda11.4-ubuntu20.04

# Set the working directory
WORKDIR /kraken

# Copy files from your host to your current working directory
COPY cpp src

# Build the application with cmake
RUN mkdir /kraken/src/build && cd /kraken/src/build && \
    cmake -DCMAKE_LIBRARY_PATH=/usr/local/cuda/lib64/stubs \
      -DCMAKE_CXX_FLAGS="-Wl,--allow-shlib-undefined" .. && \
    make

# Run the application
CMD ["/kraken/src/build/API"]