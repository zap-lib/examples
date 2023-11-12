import os from "os";
import { ZapServer, ZapUiEventType } from "zap-lib-js";
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
  "buttonU": "w", // Up
  "buttonD": "s", // Down
  "buttonR": "d", // Right
  "buttonL": "a", // Left
  "buttonA": "space", // A
  "buttonB": "ctrl", // B
  "buttonX": "esc", // X
  "buttonY": "enter", // Y
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

  onUiComponentChanged(_: string, code: string, event: ZapUiEventType, _value?: string) {
    if (event == ZapUiEventType.CLICK_DOWN) {
      robot.keyToggle(KEYMAP[code], "down");
    } else {
      robot.keyToggle(KEYMAP[code], "up");
    }
  }
}).listen();

console.log(`Motion controller server is listening on ${ip()}`);
