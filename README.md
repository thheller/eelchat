# eelchat

This is the code for [the Biff
tutorial](https://biffweb.com/docs/tutorial/build-a-chat-app/).
See [the commits](https://github.com/jacobobryant/eelchat/commits/master) to view the
code from a specific part of the tutorial.

To start the app:

1. Create a new `secrets.env` file
2. Run `bb generate-secrets` and paste the output into `secrets.env`
3. Run `bb dev`
4. Open a second terminal, run `npm install`
5. Run `npx shadow-cljs watch client` during development or `npx shadow-cljs release client` for a production build.



## CLJS Notes

### Disclaimer

I'm not actually familiar with `biff`, I only did minimal changes to the code. I did not attempt to integrate `shadow-cljs` into the facilities `biff` provides. I actually prefer to run things separately, but I'd assume it is totally possible to integrate all this more smoothly. I also didn't figure out why the Websockets never receive any messages, or why creating a secondary user can't see what the first created. The HTMX variant had the same issue, and I didn't investigate further.

### Steps taken

- I ran `npm init -y` to create a `package.json`
- I ran `npm install tailwindcss shadow-cljs` (the tailwind install `bb dev` tried to make didn't work on my machine, so I ran `tailwindcss` manually to get the CSS)
- I created the `shadow-cljs.edn` file and added the `:client` build.
- Added the `:client` alias and required dependencies to `deps.edn`
- I removed HTMX and all references to it
- The newly introduced `com.eelchat.client` namespace replaces that functionality.
- Included the generated `/js/main.js` script.

Note, for purists, the entire npm thing is **optional**.

You can run this entirely only with `clj` and never even install or touch `node/npm`. I opted to use it because I don't mind. Basically the only thing you'd change is running `clj -A:client -M -m shadow.cljs.devtools.cli watch app` instead of `npx shadow-cljs watch app`. Same for `release`. 

