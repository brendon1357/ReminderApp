import pyttsx3
import ctypes
import winsound
import time
import keyboard
from datetime import datetime


def check_late(reminder_time, current_time, current_obj, reminder_obj):
    format_time_reminder = FormatTime(reminder_time)
    military_reminder_time = format_time_reminder.convert_time()

    format_time_current = FormatTime(current_time)
    military_current_time = format_time_current.convert_time()

    if (current_obj - reminder_obj).days > 0:
        return True

    elif (current_obj - reminder_obj).days == 0:
        if military_reminder_time < military_current_time:
            return True

        elif military_reminder_time > military_current_time:
            return False

    elif (current_obj - reminder_obj).days < 0:
        return False


class FormatTime:
    def __init__(self, reminder_time):
        self.reminder_time = reminder_time

    def convert_time(self):
        actual_reminder_time = self.reminder_time[11:]

        if "AM" in actual_reminder_time:
            if actual_reminder_time[:2] == "12":
                formatted_reminder_time = str(int(actual_reminder_time[:2]) + 12) + actual_reminder_time[2:8]
                return formatted_reminder_time

            else:
                return ("0" + actual_reminder_time[:-2]).replace(" ", "")

        elif "PM" in actual_reminder_time:
            if actual_reminder_time[:2] == "12":
                formatted_reminder_time = actual_reminder_time.replace("PM", "")
                return formatted_reminder_time

            else:
                return str(int(actual_reminder_time[:1]) + 12) + ":" + actual_reminder_time[2:8]


    def handle_reminder(self):
        if ":00:00" in self.reminder_time:
            return self.reminder_time.replace(":00:00", ":00")

        else:
            return self.reminder_time.replace(":00", "")


class HandleSound:
    def __init__(self, message, freq, duration):
        self.message = message
        self.freq = freq
        self.duration = duration

    def tts_sound(self):
        engine = pyttsx3.init()
        rate = engine.getProperty('rate')
        engine.setProperty('rate', 130)
        engine.say(self.message)
        engine.runAndWait()
        engine.stop()

    def play_beep(self):
        winsound.Beep(self.freq, self.duration)


class Alarm:
    def __init__(self, message, time_alert, time_alert_object, current_date_object):
        self.message = message
        self.time_alert = time_alert
        self.time_alert_object = time_alert_object
        self.current_date_object = current_date_object

    def reminder_alert(self):
        for i in range(2):
            handle_sound_alert = HandleSound("You have a reminder alert. " + self.message, 300, 500)
            handle_sound_alert.play_beep()
            handle_sound_alert.tts_sound()
        ctypes.windll.user32.MessageBoxW(0, self.message, "Reminder Alert", 1)

    def upcoming_reminder(self):
        format_time = FormatTime(self.time_alert)
        time_alert = format_time.handle_reminder()
        handle_sound_upcoming = HandleSound("You have an upcoming reminder. " +
                                            self.message + "...Scheduled to alert at " + time_alert, 300, 500)
        handle_sound_upcoming.play_beep()
        handle_sound_upcoming.tts_sound()
        ctypes.windll.user32.MessageBoxW(0, self.message + "\n\nScheduled to alert at " +
                time_alert, "Upcoming Reminder", 1)

    def late_reminder(self):
        handle_sound_late = HandleSound("You have a late reminder. " + self.message, 300, 500)
        handle_sound_late.play_beep()
        handle_sound_late.tts_sound()
        ctypes.windll.user32.MessageBoxW(0, self.message, "Late Reminder", 1)

    def delete_reminder(self):
        with open("C:/RemindersData/Reminders.txt", "r+") as f:
            lines = f.readlines()
            f.seek(0)
            for i in lines:
                if self.time_alert not in i:
                    f.write(i)
                f.truncate()

            handle_sound_delete = HandleSound("An old reminder. " + self.message + " has been deleted.", 300, 500)
            handle_sound_delete.play_beep()
            handle_sound_delete.tts_sound()
            ctypes.windll.user32.MessageBoxW(0, "Old reminder: " + "\n\n" + self.message +
                                             " \n\nhas been deleted.", "Deleted", 1)

    def play_reminder(self):
        if self.time_alert == time.strftime("%Y-%m-%d %#I:%M:%S %p"):
            self.reminder_alert()

        elif (self.current_date_object - self.time_alert_object).days >= 15:
            self.delete_reminder()

        elif check_late(self.time_alert, time.strftime("%Y-%m-%d %#I:%M %p"),
                        self.current_date_object, self.time_alert_object):
            self.late_reminder()

        elif not check_late(self.time_alert, time.strftime("%Y-%m-%d %#I:%M %p"),
                            self.current_date_object, self.time_alert_object):
            if (self.time_alert_object - self.current_date_object).days <= 2:
                self.upcoming_reminder()


class Start:
    def check_keyboard_press(self):
        not_pressed = True

        while not_pressed:
            if keyboard.is_pressed("q") and keyboard.is_pressed("o"):
                return True

            elif keyboard.is_pressed("w") and keyboard.is_pressed("p"):
                return False

    def is_empty(self):
        with open("C:/RemindersData/Reminders.txt", "r") as reminders:
            for line in reminders:
                if line.strip():
                    return False
        return True

    def parse_reminders(self):
        datetimeFormat = "%Y-%m-%d %H:%M:%S %p"

        with open("C:/RemindersData/Reminders.txt", "r") as reminders:
            for line in reminders:
                text_message = line[line.find("(") + 1:line.find(")")]
                text_time_alert = line[line.find("[") + 1:line.find("]")]
                time_alert_object = datetime.strptime(text_time_alert, datetimeFormat)
                current_date_object = datetime.strptime(datetime.now().strftime(datetimeFormat),
                    datetimeFormat)

                handle_reminders = Alarm(text_message, text_time_alert, time_alert_object, current_date_object)

                if not self.is_empty():
                    if text_time_alert == time.strftime("%Y-%m-%d %#I:%M:%S %p"):
                        handle_reminders.reminder_alert()

                    elif (current_date_object - time_alert_object).days >= 15:
                        handle_reminders.delete_reminder()

                    elif check_late(text_time_alert, time.strftime("%Y-%m-%d %#I:%M %p"), current_date_object, time_alert_object):
                        handle_reminders.late_reminder()

                    else:
                        if (time_alert_object - current_date_object).days <= 5:
                            handle_reminders.upcoming_reminder()

                else:
                    handle_sound_empty = HandleSound("You currently have no saved reminders.", 300, 500)
                    handle_sound_empty.play_beep()
                    handle_sound_empty.tts_sound()

        if not self.is_empty():
            handle_sound_end = HandleSound("No more alerts. Waiting until next reminder period.", 0, 0)
            handle_sound_end.tts_sound()

    def run(self):
        time_list = ["10:00:00 AM", "12:00:00 PM", "2:00:00 PM", "4:00:00 PM", "6:00:00 PM", "8:00:00 PM"]
        already_played = False

        while True:
            time.sleep(1)

            if time.strftime("%#I:%M:%S %p") in time_list or not already_played:
                handle_sound_intro = HandleSound("Press Q and O to hear reminders. Or press W and P to quit.", 300, 500)
                handle_sound_intro.play_beep()
                handle_sound_intro.tts_sound()

                if self.check_keyboard_press():
                    self.parse_reminders()

                else:
                    handle_sound_intro = HandleSound("Exiting. Reminders will not be read.", 300, 0)
                    handle_sound_intro.tts_sound()

            already_played = True


if __name__ == "__main__":
    Start().run()
