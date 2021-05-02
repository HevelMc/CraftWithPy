from me.hevelmc.craftwithpy.events import PyEvent
from me.hevelmc.craftwithpy.commands import PyCommand


def register_command(cmd, pointer, usage="", description="", permission="", aliases=[]):

    # Arguments check
    if type(cmd) != str or type(aliases) != list or type(pointer).__name__ != "instancemethod":
        raise AttributeError
    if permission == "":
        permission = None
    for alias in aliases:
        if type(alias) != str:
            raise AttributeError

    PyCommand.registerNewCommand(cmd, pointer.im_class.__name__, pointer.__name__, usage, description, permission, aliases)


def register_event(event, pointer):

    # Arguments check
    if type(event) != str or type(pointer).__name__ != "instancemethod":
        raise AttributeError

    return PyEvent.registerNewEvent(event, pointer.im_class.__name__, pointer.__name__)