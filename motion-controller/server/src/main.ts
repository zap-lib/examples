import os from "os";
import { ZapServer, ZapUiComponentEvent } from "zap-lib";
import robot from "robotjs";

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
  "2131231225": "w", // Up
  "2131231222": "s", // Down
  "2131231224": "d", // Left
  "2131231223": "a", // Right
  "2131230818": "space", // A
  "2131230819": "ctrl", // B
  "2131230821": "esc", // X
  "2131230822": "enter", // Y
};

const ACC_KEYMAP: { [key: string]: [string, boolean] } = {
  "left": ["left", false],
  "right": ["right", false],
};

(new class extends ZapServer {
  onAccelerometerChanged(_: string, _x: number, y: number, _z: number) {
    if (y > 3) {
      robot.keyToggle(ACC_KEYMAP["right"][0], "down");
      ACC_KEYMAP["right"][1] = true;
    } else if (y < -3) {
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

  onUiComponentChanged(_: string, code: string, event: ZapUiComponentEvent, _value?: string) {
    if (event == ZapUiComponentEvent.CLICK_DOWN) {
      robot.keyToggle(KEYMAP[code], "down");
    } else {
      robot.keyToggle(KEYMAP[code], "up");
    }
  }
}).listen();

console.log(`Motion controller server is listening on ${ip()}`);
