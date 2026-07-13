FROM eclipse-temurin:25-jdk-noble

ENV DEBIAN_FRONTEND=noninteractive
ENV MAVEN_VERSION=3.9.9
ENV DISPLAY=:99
ENV BRAVE_PATH=/usr/bin/brave-browser

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates curl gnupg xz-utils wget bzip2 unzip \
        xvfb x11vnc fluxbox ffmpeg python3 \
        fonts-liberation libnss3 libxss1 libasound2t64 libatk-bridge2.0-0 \
        libgtk-3-0 libgbm1 libdbus-glib-1-2 \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
        | tar -xz -C /opt \
    && ln -s "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn

RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] https://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y --no-install-recommends google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL -o /tmp/firefox.tar.xz "https://download.mozilla.org/?product=firefox-latest&os=linux64&lang=en-US" \
    && tar -xf /tmp/firefox.tar.xz -C /opt \
    && rm /tmp/firefox.tar.xz \
    && ln -s /opt/firefox/firefox /usr/local/bin/firefox

# Install Microsoft Edge — driver is fetched at runtime by Selenium Manager (Selenium 4.44+).
RUN curl -fsSL https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor -o /usr/share/keyrings/microsoft.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/microsoft.gpg] https://packages.microsoft.com/repos/edge stable main" \
        > /etc/apt/sources.list.d/microsoft-edge.list \
    && apt-get update && apt-get install -y --no-install-recommends microsoft-edge-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Brave Browser
RUN curl -fsSL https://brave-browser-apt-release.s3.brave.com/brave-browser-archive-keyring.gpg > /usr/share/keyrings/brave-browser-archive-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/brave-browser-archive-keyring.gpg] https://brave-browser-apt-release.s3.brave.com/ stable main" \
        > /etc/apt/sources.list.d/brave-browser-release.list \
    && apt-get update && apt-get install -y --no-install-recommends brave-browser \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
COPY docker/entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
