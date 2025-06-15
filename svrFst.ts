

import Fastify from 'fastify'
console.log(111)
const app = Fastify({ logger: true })

app.get('/', async (request, reply) => {
  return { hello: 'world' }
})

app.get('/login', async (req, res) => {
  return { msg: 'Login page' }
})

app.listen({ port: 3000 }, (err, address) => {
  if (err) {
    console.error(err)
    process.exit(1)
  }
  console.log(`🚀 Server listening at ${address}`)
})

console.log(666)
