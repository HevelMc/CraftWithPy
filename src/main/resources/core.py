from me.hevelmc.craftwithpy.events import PyEvent
from me.hevelmc.craftwithpy.commands import PyCommand


def register_command(cmd, usage="", description="", permission="", aliases=[]):

    # Arguments check
    if type(cmd) != str:
        raise AttributeError
    if permission == "":
        permission = None
    if type(aliases) != list:
        raise AttributeError
    for alias in aliases:
        if type(alias) != str:
            raise AttributeError

    PyCommand.registerNewCommand(cmd, usage, description, permission, aliases)


def register_event(event):

    # Arguments check
    if type(event) != str:
        raise AttributeError

    return PyEvent.registerNewEvent(event)