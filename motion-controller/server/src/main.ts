import os from "os";
import robot from "robotjs";
import { MetaInfo, ZapAccelerometer, ZapServer, ZapUiEvent, ZapUiEventType } from "zap-lib-js";
import qrcode from "qrcode-terminal";

function ip() {
  const interfaces = os.networkInterfaces();

  for (let i in interfaces) {
    const found = interfaces[i];
    if (found) {
      for (let address of found) {
        if (address.family === 'IPv4' && !address.internal) {
          return address.address;
        }
      }
    }
  }

  return '0.0.0.0';
}

const KEYMAP: { [key: string]: string } = {
  "buttonU": "w", // Up
  "buttonD": "s", // Down
  "buttonR": "d", // Right
  "buttonL": "a", // Left
  "buttonA": "space", // A
  "buttonB": "control", // B
  "buttonX": "escape", // X
  "buttonY": "enter", // Y
};

const ACC_KEYMAP: { [key: string]: [string, boolean] } = {
  "left": ["left", false],
  "right": ["right", false],
};

(new class extends ZapServer {
  onAccelerometerReceived(_info: MetaInfo, data: ZapAccelerometer) {
    if (data.y > 2) {
      robot.keyToggle(ACC_KEYMAP["right"][0], "down");
      ACC_KEYMAP["right"][1] = true;
    } else if (data.y < -2) {
      robot.keyToggle(ACC_KEYMAP["left"][0], "down");
      ACC_KEYMAP["left"][1] = true;
    } else {
      if (ACC_KEYMAP["right"][1]) {
        robot.keyToggle(ACC_KEYMAP["right"][0], "up");
        ACC_KEYMAP["right"][1] = false;
      }
      if (ACC_KEYMAP["left"][1]) {
        robot.keyToggle(ACC_KEYMAP["left"][0], "up");
        ACC_KEYMAP["left"][1] = false;
      }
    }
  }

  onUiEventReceived(_info: MetaInfo, data: ZapUiEvent) {
    if (data.event == ZapUiEventType.CLICK_DOWN) {
      robot.keyToggle(KEYMAP[data.uiId], "down");
    } else {
      robot.keyToggle(KEYMAP[data.uiId], "up");
    }
  }
}).listen();

console.log('Motion controller server is listening...');
console.log('Scan the QR code below to connect your client device.')
qrcode.generate(ip());
