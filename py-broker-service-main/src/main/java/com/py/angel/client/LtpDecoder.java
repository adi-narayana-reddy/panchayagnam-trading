
package com.py.angel.client;

import com.py.angel.dto.LiveTick;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LtpDecoder {

    private static final Logger log = LoggerFactory.getLogger(LtpDecoder.class);

    public static LiveTick decode(byte[] bytes) {

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 1ï¸âƒ£ Mode
        byte mode = buffer.get(0);
        if (mode != 1) {
            return null; // not LTP
        }

        // 2ï¸âƒ£ Token (bytes 2â€“26)
        byte[] tokenBytes = new byte[25];
        buffer.position(2);
        buffer.get(tokenBytes);

        String token = new String(tokenBytes, StandardCharsets.UTF_8)
                .replace("\0", "")
                .trim();

        // 3ï¸âƒ£ Exchange Time
        buffer.position(35);
        long exchangeTime = buffer.getLong();

        // 4ï¸âƒ£ LTP (paise)
        buffer.position(43);
        long ltpPaise = buffer.getLong();
        double ltp = ltpPaise / 100.0;

        return new LiveTick(token, ltp, exchangeTime);
    }
}


