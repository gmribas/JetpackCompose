# Jetpack Compose components configured by a json file

#### Json Structure

```
{
   "id":"screen_2",
   "widget":{
      "id":"container",
      "viewClass":"LAYOUT",
      "attributes":[
      ],
      "children":[
         {
            "id":"column_container",
            "viewClass":"COLUMN",
            "attributes":[
            ],
            "children":[
               {
                  "id":"first_child",
                  "viewClass":"ROW",
                  "attributes":[
                  ],
                  "children":[
                     {
                        "id":"title",
                        "viewClass":"TEXT",
                        "attributes":[
                           {
                              "key":"TEXT",
                              "value":"Second Screen"
                           }
                        ],
                        "event":{
                           "type":"VALUE_CHANGE",
                           "action":"LIVE_DATA",
                           "bundle":"sixth_child_input"
                        },
                        "children":[
                        ]
                     }
                  ]
               }
            ]
         }
      ]
   }
}
```

##### Screens
- id
- first: is first screen
- widget: "root" widget contains the children

##### Widgets
- id
- viewClass: widget type (ROW, COLUMN, TEXT, etc)
- attributes: widget configuration attributes (width, height, padding, etc)
- children: nested widgets
- event: event related to the widget

##### Event
- type: CLICK, VALUE_CHANGE
- action: Related to click => SET_VISIBLE, START_SCREEN, TOAST, etc. Related to value change => LIVE_DATA
- bundle: actions' bundle => ie: click event starting the "screen_2"
```
"event": {
    "type": "CLICK",
    "action": "START_SCREEN",
    "bundle": "screen_2"
  }
```

---
# Project Structure

##### MainActivity

- Sets App composable content to `setContent()`
- Creates the viewModels

##### ScreenFactory

- Parses the json & builds the first screen and its widgets

##### EventFactory

- Creates the events related to the widget
